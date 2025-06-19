package org.example.hugmeexp.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.user.exception.InvalidPositiveValueException;
import org.example.hugmeexp.domain.user.exception.PhoneNumberDuplicatedException;
import org.example.hugmeexp.domain.user.exception.UserNotFoundException;
import org.example.hugmeexp.domain.user.exception.UsernameDuplicatedException;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.global.infra.auth.dto.request.LoginRequest;
import org.example.hugmeexp.global.infra.auth.dto.request.RegisterRequest;
import org.example.hugmeexp.global.infra.auth.exception.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 모든 User 리턴
    public List<User> findAll(){
        return userRepository.findAll();
    }

    // userId를 바탕으로 유저 리턴
    public User findById(long userId){
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    // name을 바탕으로 모든 User 리턴
    public List<User> findByNameContaining(String name){
        return userRepository.findByNameContaining(name);
    }

    // username을 바탕으로 User 리턴
    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    // phoneNumber를 바탕으로 User 리턴
    public Optional<User> findByPhoneNumber(String phoneNumber){
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    // 경험치 증가
    @Transactional
    public void increaseExp(long userId, int value){
        if(value <= 0) throw new InvalidPositiveValueException();
        User user = findById(userId);
        user.increaseExp(value);
    }

    // 구름조각 증가
    @Transactional
    public void increasePoint(long userId, int value){
        if(value <= 0) throw new InvalidPositiveValueException();
        User user = findById(userId);
        user.increasePoint(value);
    }

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
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(LoginFailedException::new);

        // 비밀번호 불일치시 예외를 던짐
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new LoginFailedException();
        }

        return user;
    }

    // username을 바탕으로 삭제
    @Transactional
    public void deleteByUsername(String username){
        long deletedCount = userRepository.deleteByUsername(username);
        if(deletedCount == 0) {
            throw new UserNotFoundException();
        }
    }

    // username과 phoneNumber가 중복되는지 검사
    private void validateDuplicateUser(String username, String phoneNumber) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameDuplicatedException();
        }

        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new PhoneNumberDuplicatedException();
        }
    }
}
