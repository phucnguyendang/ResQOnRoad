package com.rescue.system.repository;

import com.rescue.system.entity.Account;
import com.rescue.system.entity.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);
    boolean existsByUsername(String username);

    List<Account> findByCompanyIdIsNotNull();

    List<Account> findByCompanyIdAndRole(Long companyId, Role role);

    /**
     * Find the company account that represents a rescue company.
     * companyId here refers to rescue_companies.id (stored in accounts.company_id).
     */
    Optional<Account> findFirstByCompanyIdAndRole(Long companyId, Role role);
}
