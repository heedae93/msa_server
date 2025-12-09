package com.msa.auth.adapter.out.persistence;


import com.msa.auth.application.port.out.LoadUserPort;
import com.msa.auth.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@RequiredArgsConstructor
// 이 어댑터는 "사용자 불러오기 포트(LoadUserPort)"의 구현체입니다.
public class UserPersistenceAdapter implements LoadUserPort {

    private final UserRepository userRepository; // 실제 JPA 레포지토리

    @Override
    public Optional<User> loadUser(String username) {
        return userRepository.findByUsername(username);
    }
}
