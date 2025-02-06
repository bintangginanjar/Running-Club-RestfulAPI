package com.runclub.restful.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.runclub.restful.api.entity.RoleEntity;
import com.runclub.restful.api.mapper.ResponseMapper;
import com.runclub.restful.api.model.RoleResponse;
import com.runclub.restful.api.repository.RoleRepository;

@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> list(Authentication authentication) {
        List<RoleEntity> roles = roleRepository.findAll();

        return ResponseMapper.ToRoleResponseList(roles);
    }
}
