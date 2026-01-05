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

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            AccountRepository accountRepository,
            RescueCompanyRepository rescueCompanyRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
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

            // Create test company accounts if not exist
            if (!accountRepository.existsByUsername("company1")) {
                Account company1 = new Account();
                company1.setUsername("company1");
                company1.setPasswordHash(passwordEncoder.encode("password123"));
                company1.setFullName("Cứu Hộ Ba Đình 24/7");
                company1.setPhoneNumber("0243123456");
                company1.setEmail("badinh247@rescue.vn");
                company1.setRole(Role.COMPANY);
                company1.setCompanyId(1L);
                accountRepository.save(company1);
                System.out.println("Created test account: company1");
            }

            if (!accountRepository.existsByUsername("company2")) {
                Account company2 = new Account();
                company2.setUsername("company2");
                company2.setPasswordHash(passwordEncoder.encode("password123"));
                company2.setFullName("Cứu Hộ Hoàn Kiếm Express");
                company2.setPhoneNumber("0243234567");
                company2.setEmail("hoankiem@rescue.vn");
                company2.setRole(Role.COMPANY);
                company2.setCompanyId(2L);
                accountRepository.save(company2);
                System.out.println("Created test account: company2");
            }
        };
    }
}
