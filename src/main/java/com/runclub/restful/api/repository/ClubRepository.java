package com.runclub.restful.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.runclub.restful.api.entity.ClubEntity;
import com.runclub.restful.api.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface ClubRepository extends JpaRepository<ClubEntity, Integer> {

    Optional<ClubEntity> findByTitle(String title);

    Optional<ClubEntity> findFirstByCreatedByAndId(UserEntity user, Integer id);

    Optional<ClubEntity> findFirstByCreatedByAndTitle(UserEntity user, String title);

    List<ClubEntity> findAllByCreatedBy(UserEntity user);

}
