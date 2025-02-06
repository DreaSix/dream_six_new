package com.dream.six.service.impl;


import com.dream.six.entity.UserInfoEntity;
import com.dream.six.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserInfoService implements ReactiveUserDetailsService {

    private final UserInfoRepository userAuthRepository;


    @Override
    public Mono<UserDetails> findByUsername(String username) {
        Optional<UserInfoEntity> userAuth = userAuthRepository.findByUserNameAndIsDeletedFalse(username);

        if (userAuth.isPresent()) {
            UserInfoEntity existUser = userAuth.get();

            return Mono.just(existUser);

        } else {
            throw new UsernameNotFoundException("User doesn't exist");
        }
    }
}
