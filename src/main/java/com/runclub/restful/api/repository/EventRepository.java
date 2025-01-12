package com.runclub.restful.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.runclub.restful.api.entity.EventEntity;

public interface EventRepository extends JpaRepository<EventEntity, Integer>{

}
