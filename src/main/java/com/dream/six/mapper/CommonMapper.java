package com.dream.six.mapper;

import com.dream.six.entity.*;
import com.dream.six.utils.CommonDateUtils;
import com.dream.six.vo.request.PaymentRequestDTO;
import com.dream.six.vo.request.UserRequestVO;
import com.dream.six.vo.response.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CommonMapper {

    CommonMapper mapper = Mappers.getMapper(CommonMapper.class);

    @Mapping(target = "roles", expression = "java(mapRoles(savedUser.getRoles()))")
    @Mapping(target = "createdAt", expression = "java(savedUser.getCreatedAt() != null ? convertDate(savedUser.getCreatedAt()) : null)")
    @Mapping(target = "updatedAt", expression = "java(savedUser.getUpdatedAt() != null ? convertDate(savedUser.getUpdatedAt()) : null)")
    UserResponseVO convertUserInfoEntityToUserResponse(UserInfoEntity savedUser);

    default String convertDate(java.sql.Timestamp date) {
        return CommonDateUtils.getTimestampString(date);
    }


    @Mapping(target = "roles", ignore = true)
    UserInfoEntity convertUserRequestToUserInfoEntity(UserRequestVO request);



    default List<RoleDetail> mapRoles(List<RoleEntity> roleEntities) {
        List<RoleDetail> roleDetails = new ArrayList<>();
        if (roleEntities != null) {
            roleDetails = roleEntities.stream()
                    .map(roleEntity -> {
                        RoleDetail roleDetail = new RoleDetail();
                        roleDetail.setRoleId(roleEntity.getId());
                        roleDetail.setRoleName(roleEntity.getName());
                        return roleDetail;
                    })
                    .toList();
        }
        return roleDetails;
    }


    PaymentResponseDTO convertEntityToPaymentResponseDTO(Payment savedPayment);


    TransactionResponseDTO convertEntityToTransactionResponseDTO(Transaction savedTransaction);
}


