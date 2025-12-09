package com.msa.auth.application.port.out;

import com.msa.auth.domain.User;

import java.util.Optional;

public interface LoadUserPort {

    Optional<User> loadUser(String username);
}
