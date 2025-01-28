package com.dream.six.service.impl;

import com.dream.six.config.JwtService;
import com.dream.six.entity.TokenEntity;
import com.dream.six.entity.UserAuthEntity;
import com.dream.six.entity.UserInfoEntity;
import com.dream.six.enums.TokenType;
import com.dream.six.utils.PasswordUtils;
import com.dream.six.constants.ErrorMessageConstants;
import com.dream.six.exception.InvalidPasswordException;
import com.dream.six.exception.ResourceNotFoundException;
import com.dream.six.repository.TokenRepository;
import com.dream.six.repository.UserAuthRepository;
import com.dream.six.service.LoginService;
import com.dream.six.service.RoleService;
import com.dream.six.vo.request.LoginRequestVO;
import com.dream.six.vo.request.ValidateTokenRequestVO;
import com.dream.six.vo.response.JwtResponseVO;
import com.dream.six.vo.response.PermissionsResponseVO;
import com.dream.six.vo.response.RoleDetail;
import com.dream.six.vo.response.ValidateTokenResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static com.dream.six.entity.TokenEntity.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserAuthRepository userAuthRepository;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final RoleService roleService;


    @Override
    public JwtResponseVO authenticateUser(LoginRequestVO loginRequest) throws ExecutionException, InterruptedException {

        UserAuthEntity userAuth = userAuthRepository.findByUserNameAndIsDeletedFalse(loginRequest.getUserName())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessageConstants.USER_NOT_FOUND));

        boolean validPassword = PasswordUtils.verifyPassword(loginRequest.getPassword(), userAuth.getEncodedPassword());

        if (!validPassword) {
            throw new InvalidPasswordException("Invalid password");
        }

      /*  Mono<Authentication> authentication = reactiveAuthenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));

        authentication.subscribe(auth -> {
            SecurityContextHolder.getContext().setAuthentication(auth);
        });*/

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUserName(),
                            loginRequest.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new ResourceNotFoundException(String.format(
                    "User login failed: %s", loginRequest.getUserName()));
        }

        var jwtToken = jwtService.generateToken(userAuth.getUserInfo());
        var refreshToken = jwtService.generateRefreshToken(userAuth.getUserInfo());

        this.revokeAllUserTokens(userAuth.getUserInfo());

        saveUserToken(userAuth.getUserInfo(), jwtToken);

        List<RoleDetail> roles = userAuth.getUserInfo().getRoles().stream().map(role -> {
                    RoleDetail roleDetail = new RoleDetail();
                    roleDetail.setRoleId(role.getId());
                    roleDetail.setRoleName(role.getName());
                    return roleDetail;
                })
                .toList();

        Set<PermissionsResponseVO> permissionsResponseVOS = roleService.getAllPermissionsByUserId(userAuth.getUserId());

        return new JwtResponseVO(jwtToken, refreshToken, userAuth.getUserInfo().getUsername(), userAuth.getUserInfo().getId(), roles, permissionsResponseVOS);
    }

    @Override
    public ValidateTokenResponseVO validateToken(ValidateTokenRequestVO request) {

        try {
            var token = tokenRepository.findByJwtToken(request.getToken());
            if (token.isEmpty()) {
                throw new InvalidPasswordException("Invalid token");
            }
            boolean isTokenValid = jwtService.isTokenValid(request.getToken(), token.get().getUserInfo());
            if (isTokenValid) {
                return ValidateTokenResponseVO.builder()
                        .status(200)
                        .data(true)
                        .build();
            } else {
                return ValidateTokenResponseVO.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .data(false)
                        .build();
            }
        } catch (Exception e) {
            return ValidateTokenResponseVO.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .data(false)
                    .build();
        }
    }

    private void saveUserToken(UserInfoEntity user, String jwtToken) {
        TokenEntity token = new TokenEntity();
        token.setJwtToken(jwtToken);
        token.setUserInfo(user);
        token.setExpired(false);
        token.setRevoked(false);
        token.setTokenType(TokenType.BEARER);
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(UserInfoEntity user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

}
