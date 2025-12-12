package com.msa.auth.application.port.out;

import com.msa.auth.domain.User;

public interface SaveUserPort {
    void saveUser(User user);
}
