package org.example.hugmeexp.global.common.repository;

import org.example.hugmeexp.global.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // username이 존재하는지 확인
    boolean existsByUsername(String username);

    // phoneNumber가 존재하는지 확인
    boolean existsByPhoneNumber(String phoneNumber);

    // username을 바탕으로 User 리턴
    Optional<User> findByUsername(String username);

    // name을 바탕으로 User 리턴(사용 지양)
    Optional<User> findByName(String name);

    // username을 바탕으로 User 삭제
    void deleteByUsername(String username);

    // id를 바탕으로 User 리턴
    Optional<User> findById(long userId);

    // name을 바탕으로 모든 User 리턴
    List<User> findByNameContaining(String name);

    // phoneNumber를 바탕으로 User 리턴
    Optional<User> findByPhoneNumber(String phoneNumber);
}
