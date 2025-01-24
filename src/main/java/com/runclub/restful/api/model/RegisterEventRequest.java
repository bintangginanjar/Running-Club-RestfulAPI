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
public class RegisterEventRequest {

    private String clubId;
    
    @NotBlank
    private String name;
    
    @NotBlank
    private String startTime;
    
    @NotBlank
    private String endTime;
    
    @NotBlank
    private String type;
    
    @NotBlank
    private String photoUrl;

}
