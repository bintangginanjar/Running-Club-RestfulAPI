package com.runclub.restful.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClubResponse {

    private Integer id;

    private String title;

    private String photoUrl;

    private String content;

}
