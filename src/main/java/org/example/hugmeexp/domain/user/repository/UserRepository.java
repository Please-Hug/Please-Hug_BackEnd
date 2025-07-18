package org.example.hugmeexp.domain.user.repository;

import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // username이 존재하는지 확인
    boolean existsByUsername(String username);

    // phoneNumber가 존재하는지 확인
    boolean existsByPhoneNumber(String phoneNumber);

    // username을 바탕으로 User 리턴
    Optional<User> findByUsername(String username);

    // username을 바탕으로 User 삭제
    Integer deleteByUsername(String username);

    // name을 바탕으로 모든 User 리턴
    List<User> findByNameContaining(String name);

    // phoneNumber를 바탕으로 User 리턴
    Optional<User> findByPhoneNumber(String phoneNumber);

    @Query("SELECT u FROM User u JOIN FETCH u.userMissionGroupList umg JOIN FETCH umg.missionGroup ORDER BY u.exp DESC")
    List<User> findAllByOrderByExpDesc();
}
