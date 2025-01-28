package com.dream.six.service;


import com.dream.six.vo.request.LoginRequestVO;
import com.dream.six.vo.request.ValidateTokenRequestVO;
import com.dream.six.vo.response.JwtResponseVO;
import com.dream.six.vo.response.ValidateTokenResponseVO;

import java.util.concurrent.ExecutionException;

public interface LoginService {

    JwtResponseVO authenticateUser(LoginRequestVO loginRequest) throws ExecutionException, InterruptedException;

    ValidateTokenResponseVO validateToken(ValidateTokenRequestVO request);

}
