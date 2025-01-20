package com.runclub.restful.api.mapper;

import com.runclub.restful.api.entity.ClubEntity;
import com.runclub.restful.api.entity.UserEntity;
import com.runclub.restful.api.model.ClubResponse;
import com.runclub.restful.api.model.UserResponse;

public class ResponseMapper {

    public static UserResponse ToUserResponseMapper(UserEntity user) {
        return UserResponse.builder()
                .username(user.getUsername())              
                .build();
    }

    public static ClubResponse ToClubResponseMapper(ClubEntity club) {
        return ClubResponse.builder()
                .title(club.getTitle())
                .photoUrl(club.getPhotoUrl())
                .content(club.getContent())
                .build();
    }
}
