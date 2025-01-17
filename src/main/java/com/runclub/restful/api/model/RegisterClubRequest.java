package com.runclub.restful.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterClubRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String photoUrl;

    @NotBlank
    private String content;

}
