package com.runclub.restful.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.runclub.restful.api.entity.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer>{

    Optional<RoleEntity> findByName(String name);

}
