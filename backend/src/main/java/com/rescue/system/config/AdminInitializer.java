package com.rescue.system.config;

import com.rescue.system.entity.Account;
import com.rescue.system.entity.Role;
import com.rescue.system.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if admin account exists
        if (accountRepository.findByUsername("admin").isEmpty()) {
            // Create admin account
            Account admin = new Account();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("password123"));
            admin.setFullName("Admin System");
            admin.setPhoneNumber("0987654321");
            admin.setEmail("admin@resqonroad.vn");
            admin.setRole(Role.ADMIN);

            accountRepository.save(admin);
            System.out.println("✓ Admin account created: username=admin, password=password123");
        } else {
            System.out.println("✓ Admin account already exists");
        }
    }
}
