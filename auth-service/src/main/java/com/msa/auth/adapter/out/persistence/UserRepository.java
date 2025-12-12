package com.msa.auth.adapter.out.persistence;

import com.msa.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// 외부(Service)에서는 이 인터페이스를 절대 모릅니다.
// 오직 같은 패키지에 있는 UserPersistenceAdapter만 갖다 씁니다.
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}
