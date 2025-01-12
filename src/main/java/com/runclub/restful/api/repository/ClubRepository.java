package com.runclub.restful.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.runclub.restful.api.entity.ClubEntity;

public interface ClubRepository extends JpaRepository<ClubEntity, Integer> {

}
