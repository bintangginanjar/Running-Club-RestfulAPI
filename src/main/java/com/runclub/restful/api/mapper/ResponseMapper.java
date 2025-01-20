package com.runclub.restful.api.mapper;

import java.util.List;
import java.util.stream.Collectors;

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

    public static List<ClubResponse> ToClubResponseListMapper(List<ClubEntity> clubs) {
        return clubs.stream()
                    .map(p -> new ClubResponse(
                            p.getId(),
                            p.getTitle(),
                            p.getContent(),
                            p.getPhotoUrl()
                    )).collect(Collectors.toList());
    }
}
