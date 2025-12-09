package com.msa.auth.application.port.in;

import com.msa.auth.adapter.in.web.dto.LoginRequestDto;

public interface LoginUseCase {

    String login(LoginRequestDto command);
}
