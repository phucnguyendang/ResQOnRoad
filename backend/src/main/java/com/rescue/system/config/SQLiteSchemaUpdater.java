package com.rescue.system.config;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * SQLite schema updater for environments that already have an existing DB file.
 *
 * Hibernate ddl-auto=update is not always able to add columns reliably on SQLite,
 * so we patch missing columns at startup in a safe, idempotent way.
 */
@Component
public class SQLiteSchemaUpdater implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public SQLiteSchemaUpdater(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Community posts: soft-delete support
        ensureColumnExists(
                "community_posts",
                "is_deleted",
            "INTEGER DEFAULT 0"
        );
        ensureColumnExists(
                "community_posts",
                "deleted_at",
                "TIMESTAMP NULL"
        );

        // Backfill NULL -> 0 for existing rows
        backfillNullToZero("community_posts", "is_deleted");

        // Community comments: close comment support
        ensureColumnExists(
                "community_comments",
                "is_closed",
            "INTEGER DEFAULT 0"
        );

        backfillNullToZero("community_comments", "is_closed");
    }

    private void ensureColumnExists(String tableName, String columnName, String columnDefinition) {
        try {
            Set<String> columns = getColumnNames(tableName);
            if (columns.isEmpty()) {
                // Table not created yet; Hibernate will create it later.
                return;
            }

            String needle = columnName.toLowerCase(Locale.ROOT);
            if (columns.contains(needle)) {
                return;
            }

            jdbcTemplate.execute(
                    "ALTER TABLE " + tableName +
                            " ADD COLUMN " + columnName + " " + columnDefinition
            );

        } catch (Exception ex) {
            // Keep the app running even if migration fails.
            // The error will be visible in logs due to server.error.include-stacktrace=true.
            System.err.println(
                    "[SQLiteSchemaUpdater] Failed to ensure column " + tableName + "." + columnName + ": " + ex.getMessage()
            );
        }
    }

    private Set<String> getColumnNames(String tableName) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("PRAGMA table_info('" + tableName + "')");
        Set<String> names = new HashSet<>();
        for (Map<String, Object> row : rows) {
            Object value = row.get("name");
            if (value != null) {
                names.add(String.valueOf(value).toLowerCase(Locale.ROOT));
            }
        }
        return names;
    }

    private void backfillNullToZero(String tableName, String columnName) {
        try {
            Set<String> columns = getColumnNames(tableName);
            if (!columns.contains(columnName.toLowerCase(Locale.ROOT))) {
                return;
            }
            jdbcTemplate.update(
                    "UPDATE " + tableName + " SET " + columnName + " = 0 WHERE " + columnName + " IS NULL"
            );
        } catch (Exception ex) {
            System.err.println(
                    "[SQLiteSchemaUpdater] Failed to backfill " + tableName + "." + columnName + ": " + ex.getMessage()
            );
        }
    }
}
