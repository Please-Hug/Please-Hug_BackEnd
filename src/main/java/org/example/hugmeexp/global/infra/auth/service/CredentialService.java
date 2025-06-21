package org.example.hugmeexp.global.infra.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.exception.PhoneNumberDuplicatedException;
import org.example.hugmeexp.domain.user.exception.UsernameDuplicatedException;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.example.hugmeexp.global.infra.auth.dto.request.LoginRequest;
import org.example.hugmeexp.global.infra.auth.dto.request.RegisterRequest;
import org.example.hugmeexp.global.infra.auth.exception.LoginFailedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CredentialService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @Transactional
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

    // 로그인(username과 password 매칭)
    @Transactional(readOnly = true)
    public User login(LoginRequest request) {

        // 아이디를 DB에서 찾을 수 없다면 예외를 던짐
        User findUser = userRepository.findByUsername(request.getUsername())
                .orElseThrow(LoginFailedException::new);

        // 비밀번호 불일치시 예외를 던짐
        if (!passwordEncoder.matches(request.getPassword(), findUser.getPassword())) {
            throw new LoginFailedException();
        }

        return findUser;
    }

    // username과 phoneNumber가 중복되는지 검사
    private void validateDuplicateUser(String username, String phoneNumber) {
        if (userRepository.existsByUsername(username)) throw new UsernameDuplicatedException();
        if (userRepository.existsByPhoneNumber(phoneNumber)) throw new PhoneNumberDuplicatedException();
    }
}
