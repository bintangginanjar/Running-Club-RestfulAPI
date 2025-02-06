package com.runclub.restful.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.runclub.restful.api.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByUsername(String username);
    
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update users_roles u set u.role_id = ?2 where u.user_id = ?1", nativeQuery = true)
    int updateUserRole(Long userId, Long roleId);
    
}
