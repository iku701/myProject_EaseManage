package edu.du.myproject1101_1.repository;

import edu.du.myproject1101_1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // 이메일 중복 여부 확인 메서드 추가
    boolean existsByEmail(String email);
    // 개인정보 업데이트용 이메일 중복여부 확인 메서드

}
