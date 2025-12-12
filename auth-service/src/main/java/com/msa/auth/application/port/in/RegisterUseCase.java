package com.msa.auth.application.port.in;


import com.msa.auth.adapter.in.web.dto.SignupRequestDto;

public interface RegisterUseCase {
    void registerUser(SignupRequestDto command);
}
