package com.runclub.restful.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateEventRequest {

    @JsonIgnore
    @NotBlank
    private String clubId;

    @JsonIgnore
    @NotBlank
    private String eventId;

    private String name;
    
    private String startTime;
    
    private String endTime;
    
    private String type;
    
    private String photoUrl;

}
