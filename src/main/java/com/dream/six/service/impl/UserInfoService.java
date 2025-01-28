package com.dream.six.service.impl;


import com.dream.six.entity.UserAuthEntity;
import com.dream.six.repository.UserAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class UserInfoService implements ReactiveUserDetailsService {

    private final UserAuthRepository userAuthRepository;

    @Autowired
    public UserInfoService(UserAuthRepository userAuthRepository) {
        this.userAuthRepository = userAuthRepository;
    }


    @Override
    public Mono<UserDetails> findByUsername(String username) {
        Optional<UserAuthEntity> userAuth = userAuthRepository.findByUserNameAndIsDeletedFalse(username);

        if (userAuth.isPresent()) {
            UserAuthEntity existUser = userAuth.get();
            UserDetails userDetails = existUser.getUserInfo();

            return Mono.just(userDetails);

        } else {
            throw new UsernameNotFoundException("User doesn't exist");
        }
    }
}
