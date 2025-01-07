package com.runclub.restful.api.mapper;

import com.runclub.restful.api.entity.UserEntity;
import com.runclub.restful.api.model.UserResponse;

public class UserResponseMapper {

    public static UserResponse ToUserResponseMapper(UserEntity user) {
        return UserResponse.builder()
                .username(user.getUsername())                
                .build();
    }

}
