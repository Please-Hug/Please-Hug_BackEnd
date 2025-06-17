package org.example.hugmeexp.global.common.service;

import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.global.common.repository.UserRepository;
import org.example.hugmeexp.global.entity.User;
import org.example.hugmeexp.global.infra.auth.dto.request.LoginRequest;
import org.example.hugmeexp.global.infra.auth.dto.request.RegisterRequest;
import org.example.hugmeexp.global.infra.auth.exception.LoginFailedException;
import org.example.hugmeexp.global.infra.auth.exception.PhoneNumberDuplicatedException;
import org.example.hugmeexp.global.infra.auth.exception.UsernameDuplicatedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public User registerNewUser(RegisterRequest request) {

        // 중복 예외 처리
        validateDuplicateUser(request.getUsername(), request.getPhoneNumber());

        // 패스워드 bcrypt 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 유저 저장
        User user = User.createUser(request.getUsername(), encodedPassword, request.getName(), request.getPhoneNumber());
        userRepository.save(user);

        return user;
    }

    public User login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(LoginFailedException::new); // 아이디를 DB에서 찾을 수 없다면 예외를 던짐

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new LoginFailedException(); // 비밀번호 불일치시 예외를 던짐
        }

        return user;
    }

    private void validateDuplicateUser(String username, String phoneNumber) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameDuplicatedException();
        }

        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new PhoneNumberDuplicatedException();
        }
    }

}
