package com.rescue.system.config;

import com.rescue.system.entity.Account;
import com.rescue.system.entity.Role;
import com.rescue.system.entity.RescueCompany;
import com.rescue.system.entity.ProfileStatus;
import com.rescue.system.repository.AccountRepository;
import com.rescue.system.repository.RescueCompanyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            AccountRepository accountRepository,
            RescueCompanyRepository rescueCompanyRepository,
            PasswordEncoder passwordEncoder,
            JdbcTemplate jdbcTemplate) {
        return args -> {
            ensureDbConstraints(jdbcTemplate);

            // Create rescue companies first
            if (rescueCompanyRepository.count() == 0) {
                RescueCompany company1 = new RescueCompany();
                company1.setName("Cứu Hộ Ba Đình 24/7");
                company1.setAddress("Số 123 Đường Hoàng Hoa Thám, Ba Đình, Hà Nội");
                company1.setPhone("0243123456");
                company1.setEmail("badinh247@rescue.vn");
                company1.setLatitude(21.0285);
                company1.setLongitude(105.8542);
                company1.setServiceRadius(50.0);
                company1.setIsActive(true);
                company1.setAverageRating(4.8);
                company1.setTotalReviews(156);
                company1.setDescription(
                        "Chuyên cứu hộ ô tô, xe máy 24/7. Đội ngũ chuyên nghiệp, trang thiết bị hiện đại.");
                company1.setBusinessLicense("GP-BD-2023-001");
                company1.setIsVerified(true);
                company1.setProfileStatus(ProfileStatus.APPROVED);
                company1.setHotline("0243123456");
                company1.setOperatingHours("24/7");
                rescueCompanyRepository.save(company1);
                System.out.println("Created rescue company: " + company1.getName());

                RescueCompany company2 = new RescueCompany();
                company2.setName("Cứu Hộ Hoàn Kiếm Express");
                company2.setAddress("Số 45 Phố Hàng Bài, Hoàn Kiếm, Hà Nội");
                company2.setPhone("0243234567");
                company2.setEmail("hoankiem@rescue.vn");
                company2.setLatitude(21.0245);
                company2.setLongitude(105.8516);
                company2.setServiceRadius(40.0);
                company2.setIsActive(true);
                company2.setAverageRating(4.5);
                company2.setTotalReviews(89);
                company2.setDescription("Dịch vụ cứu hộ nhanh chóng tại khu vực trung tâm.");
                company2.setBusinessLicense("GP-HK-2023-002");
                company2.setIsVerified(true);
                company2.setProfileStatus(ProfileStatus.APPROVED);
                company2.setHotline("0243234567");
                company2.setOperatingHours("24/7");
                rescueCompanyRepository.save(company2);
                System.out.println("Created rescue company: " + company2.getName());
            }

            // Create test user accounts if not exist
            if (!accountRepository.existsByUsername("user1")) {
                Account user1 = new Account();
                user1.setUsername("user1");
                user1.setPasswordHash(passwordEncoder.encode("password123"));
                user1.setFullName("Nguyễn Văn A");
                user1.setPhoneNumber("0901234567");
                user1.setEmail("user1@test.com");
                user1.setRole(Role.USER);
                accountRepository.save(user1);
                System.out.println("Created test account: user1");
            }

            // Enforce: every rescue company must have exactly one COMPANY account.
            // Also enforce: only COMPANY accounts can have company_id.
            List<Account> accountsWithCompanyId = accountRepository.findByCompanyIdIsNotNull();
            for (Account a : accountsWithCompanyId) {
                if (a.getRole() != Role.COMPANY) {
                    throw new IllegalStateException(
                            "Invalid data: account id=" + a.getId() + " has company_id but role=" + a.getRole());
                }
                if (a.getCompanyId() != null && !rescueCompanyRepository.existsById(a.getCompanyId())) {
                    throw new IllegalStateException(
                            "Invalid data: COMPANY account id=" + a.getId() + " references missing rescue_company id="
                                    + a.getCompanyId());
                }
            }

            final String defaultCompanyPassword = "password123";
            for (RescueCompany c : rescueCompanyRepository.findAll()) {
                List<Account> companyAccounts = accountRepository.findByCompanyIdAndRole(c.getId(), Role.COMPANY);
                if (companyAccounts.size() > 1) {
                    throw new IllegalStateException(
                            "Invalid data: rescue_company id=" + c.getId() + " has " + companyAccounts.size()
                                    + " COMPANY accounts (expected exactly 1)");
                }

                if (companyAccounts.isEmpty()) {
                    // Auto-create missing company account (dev/test convenience).
                    String baseUsername = "company" + c.getId();
                    String username = baseUsername;
                    int suffix = 2;
                    while (accountRepository.existsByUsername(username)) {
                        username = baseUsername + "_" + suffix;
                        suffix++;
                    }

                    Account companyAccount = new Account();
                    companyAccount.setUsername(username);
                    companyAccount.setPasswordHash(passwordEncoder.encode(defaultCompanyPassword));
                    companyAccount.setFullName(c.getName());
                    companyAccount.setPhoneNumber(c.getPhone());
                    companyAccount.setEmail(c.getEmail());
                    companyAccount.setRole(Role.COMPANY);
                    companyAccount.setCompanyId(c.getId());
                    accountRepository.save(companyAccount);
                    System.out.println(
                            "Auto-created COMPANY account '" + username + "' for rescue company id=" + c.getId());
                }
            }

            // Create admin account if not exist
            if (!accountRepository.existsByUsername("admin")) {
                Account admin = new Account();
                admin.setUsername("admin");
                admin.setPasswordHash(passwordEncoder.encode("admin123"));
                admin.setFullName("System Administrator");
                admin.setPhoneNumber("0900000001");
                admin.setEmail("admin@resqonroad.vn");
                admin.setRole(Role.ADMIN);
                accountRepository.save(admin);
                System.out.println("Created admin account: admin");
            }
        };
    }

            private void ensureDbConstraints(JdbcTemplate jdbcTemplate) {
            // Fail fast on existing invalid rows before attempting to add constraints.
            Integer nonCompanyHasCompanyId = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM accounts WHERE company_id IS NOT NULL AND role <> 'COMPANY'",
                Integer.class);
            if (nonCompanyHasCompanyId != null && nonCompanyHasCompanyId > 0) {
                throw new IllegalStateException(
                    "Invalid data: found " + nonCompanyHasCompanyId
                        + " non-COMPANY accounts with company_id set (must be NULL)");
            }

            Integer companyMissingCompanyId = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM accounts WHERE role = 'COMPANY' AND company_id IS NULL",
                Integer.class);
            if (companyMissingCompanyId != null && companyMissingCompanyId > 0) {
                throw new IllegalStateException(
                    "Invalid data: found " + companyMissingCompanyId
                        + " COMPANY accounts with company_id NULL (must be set)");
            }

            Integer invalidCompanyRef = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM accounts a LEFT JOIN rescue_companies c ON c.id = a.company_id "
                    + "WHERE a.role = 'COMPANY' AND a.company_id IS NOT NULL AND c.id IS NULL",
                Integer.class);
            if (invalidCompanyRef != null && invalidCompanyRef > 0) {
                throw new IllegalStateException(
                    "Invalid data: found " + invalidCompanyRef
                        + " COMPANY accounts referencing missing rescue_companies rows");
            }

            Integer duplicateCompanyAccounts = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM (SELECT company_id FROM accounts WHERE role = 'COMPANY' AND company_id IS NOT NULL "
                    + "GROUP BY company_id HAVING COUNT(*) > 1)",
                Integer.class);
            if (duplicateCompanyAccounts != null && duplicateCompanyAccounts > 0) {
                throw new IllegalStateException(
                    "Invalid data: found " + duplicateCompanyAccounts
                        + " rescue_companies with more than one COMPANY account");
            }

            // Enforce at-most-one account per rescue company.
            // Note: SQLite UNIQUE allows multiple NULLs, but we also keep company_id NULL for USER/ADMIN.
            jdbcTemplate.execute(
                "CREATE UNIQUE INDEX IF NOT EXISTS uk_accounts_company_id ON accounts(company_id) WHERE company_id IS NOT NULL");

            // Enforce role/company_id consistency and existence of referenced rescue_company at DB level.
            jdbcTemplate.execute("CREATE TRIGGER IF NOT EXISTS trg_accounts_company_role_insert "
                + "BEFORE INSERT ON accounts "
                + "FOR EACH ROW BEGIN "
                + "  SELECT CASE "
                + "    WHEN NEW.role = 'COMPANY' AND NEW.company_id IS NULL THEN RAISE(ABORT, 'COMPANY account must have company_id') "
                + "    WHEN NEW.role <> 'COMPANY' AND NEW.company_id IS NOT NULL THEN RAISE(ABORT, 'Only COMPANY accounts can have company_id') "
                + "  END; "
                + "  SELECT CASE "
                + "    WHEN NEW.company_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM rescue_companies c WHERE c.id = NEW.company_id) "
                + "      THEN RAISE(ABORT, 'company_id must reference an existing rescue_company') "
                + "  END; "
                + "END;");

            jdbcTemplate.execute("CREATE TRIGGER IF NOT EXISTS trg_accounts_company_role_update "
                + "BEFORE UPDATE OF role, company_id ON accounts "
                + "FOR EACH ROW BEGIN "
                + "  SELECT CASE "
                + "    WHEN NEW.role = 'COMPANY' AND NEW.company_id IS NULL THEN RAISE(ABORT, 'COMPANY account must have company_id') "
                + "    WHEN NEW.role <> 'COMPANY' AND NEW.company_id IS NOT NULL THEN RAISE(ABORT, 'Only COMPANY accounts can have company_id') "
                + "  END; "
                + "  SELECT CASE "
                + "    WHEN NEW.company_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM rescue_companies c WHERE c.id = NEW.company_id) "
                + "      THEN RAISE(ABORT, 'company_id must reference an existing rescue_company') "
                + "  END; "
                + "END;");
            }
}
