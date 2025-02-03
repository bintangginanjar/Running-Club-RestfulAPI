package com.runclub.restful.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleModifyRequest {

    @Size(max = 128)
    private String username;

    @NotBlank
    @Size(max = 16)
    private String role;

}
