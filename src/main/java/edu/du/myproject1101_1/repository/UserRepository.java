package edu.du.myproject1101_1.repository;

import edu.du.myproject1101_1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Username으로 User 검색 메서드 추가 (중복 가능성 고려)
    Optional<User> findByUsername(String username);
}

