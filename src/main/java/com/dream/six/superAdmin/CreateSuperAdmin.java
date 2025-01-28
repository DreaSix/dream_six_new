//package com.dream.six.superAdmin;
//
//import com.dream.six.entity.UserInfoEntity;
//import com.dream.six.repository.UserInfoRepository;
//import com.dream.six.utils.PasswordUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;
//
//@Component
//@Slf4j
//public class CreateSuperAdmin implements CommandLineRunner {
//
//    private final UserInfoRepository userInfoRepository;
//    @Value("${super.admin.firstName}")
//    private String firstName;
//    @Value("${super.admin.lastName}")
//    private String lastName;
//    @Value("${super.admin.email}")
//    private String email;
//
//    @Autowired
//    public CreateSuperAdmin(UserInfoRepository userInfoRepository
//                          ) {
//        this.userInfoRepository = userInfoRepository;
//    }
//
//    @Override
//    public void run(String... args) {
//        Optional<UserInfoEntity> userInfoEntity = userInfoRepository.findByEmailAndIsDeletedFalse(email);
//
//
//        if (userInfoEntity.isEmpty()) {
//            String password = PasswordUtils.generateDummyPassword();
//            UserInfoEntity userInfo = new UserInfoEntity();
//            userInfo.setFirstName(firstName);
//            userInfo.setLastName(lastName);
//            userInfo.setEmail(email);
//            userInfo.setUserName(email);
//            userInfo.setPassword(password);
//            System.out.println(password);
//            userInfo.setEncodedPassword(PasswordUtils.hashPassword(password));
//            userInfo.setRoleEnum(RoleEnum.SUPER_ADMIN);
//            userInfoRepository.save(userInfo);
//
//
//
//        }
//    }
//
//
//}
//
//
