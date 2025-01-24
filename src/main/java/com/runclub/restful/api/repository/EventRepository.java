package com.runclub.restful.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.runclub.restful.api.entity.ClubEntity;
import com.runclub.restful.api.entity.EventEntity;
import com.runclub.restful.api.entity.UserEntity;

import java.util.List;


public interface EventRepository extends JpaRepository<EventEntity, Integer>{

    Optional<EventEntity> findByName(String eventName);

    Optional<EventEntity> findFirstByClubAndId(ClubEntity club, Integer id);

    List<EventEntity> findByCreatedBy(UserEntity createdBy);

}
