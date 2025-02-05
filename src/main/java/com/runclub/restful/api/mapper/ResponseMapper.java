package com.runclub.restful.api.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.runclub.restful.api.entity.ClubEntity;
import com.runclub.restful.api.entity.EventEntity;
import com.runclub.restful.api.entity.RoleEntity;
import com.runclub.restful.api.entity.UserEntity;
import com.runclub.restful.api.model.ClubResponse;
import com.runclub.restful.api.model.EventResponse;
import com.runclub.restful.api.model.RoleResponse;
import com.runclub.restful.api.model.UserResponse;

public class ResponseMapper {

    public static UserResponse ToUserResponseMapper(UserEntity user, List<RoleResponse> role) {        
        return UserResponse.builder()                
                .username(user.getUsername())
                .role(role)
                .build();
    }

    public static List<UserResponse> ToUserResponseListMapper(List<UserEntity> users) {        
        return users.stream()
                    .map(
                        p -> new UserResponse(
                            p.getUsername(),
                            p.getRoles().stream()
                                        .map(
                                            e -> new RoleResponse(
                                                e.getName()
                                            )).collect(Collectors.toList())
                        )).collect(Collectors.toList());
    }

    public static List<RoleResponse> ToRoleResponseList(List<RoleEntity> roles) {
        return roles.stream()
                    .map(
                        p -> new RoleResponse(
                            p.getName()
                        )).collect(Collectors.toList());
    }

    public static ClubResponse ToClubResponseMapper(ClubEntity club) {
        return ClubResponse.builder()
                .id(club.getId())
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

    public static EventResponse ToEventResponseMapper(EventEntity event) {
        return EventResponse.builder()
                .id(event.getId())
                .name(event.getName())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .type(event.getType())
                .photoUrl(event.getPhotoUrl())
                .build();
    }

    public static List<EventResponse> ToEventResponseListMapper(List<EventEntity> events) {
        return events.stream()
                    .map(p -> new EventResponse(
                            p.getId(),
                            p.getName(),
                            p.getStartTime(),
                            p.getEndTime(),
                            p.getType(),
                            p.getPhotoUrl()
                    )).collect(Collectors.toList());
    }    
}
