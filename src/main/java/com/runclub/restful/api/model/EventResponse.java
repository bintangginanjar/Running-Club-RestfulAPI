package com.runclub.restful.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventResponse {
    
    private Integer id;

    private String name;
    
    private String startTime;
    
    private String endTime;
    
    private String type;
    
    private String photoUrl;

}
