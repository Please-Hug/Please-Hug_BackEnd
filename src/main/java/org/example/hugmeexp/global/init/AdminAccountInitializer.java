//package org.example.hugmeexp.global.init;
//
//import lombok.RequiredArgsConstructor;
//import org.example.hugmeexp.domain.user.entity.User;
//import org.example.hugmeexp.domain.user.enums.UserRole;
//import org.example.hugmeexp.domain.user.repository.UserRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.password.PasswordEncoder;
//

/**
 * Admin 계정 생성 클래스
 * 전체 주석 풀고 application run 딱 한번만 하면 admin 계정 생성 완료
 * 이후에는 전체 주석 처리해서 실해 안 되게 해야함
 * 아니면 폰번호 중복이라고 에러 발생
 */

//@Configuration
//@RequiredArgsConstructor
//public class AdminAccountInitializer {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Bean
//    public CommandLineRunner createAdminAccount() {
//        return args -> {
//            String adminUsername = "admin";
//            if (userRepository.existsByUsername(adminUsername)) {
//                return;
//            }
//
//            // admin 계정 생성
//            User admin = User.builder()
//                    .username("admin01")
//                    .password(passwordEncoder.encode("123456789")) // 비밀번호는 bcrypt로 암호화
//                    .name("관리자")
//                    .phoneNumber("01000000000")
//                    .build();
//
//            admin.changeRole(UserRole.ADMIN); // 여기서 역할을 ADMIN으로 설정
//
//            userRepository.save(admin);
//            System.out.println("✅ admin 계정이 생성되었습니다.");
//        };
//    }
//}