package com.smileShark.entity.school.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class SchoolLoginResponse {
    private String access_token;
    private Integer expires_in;
    private String token_type;
    private String scope;
    private String refresh_token;
}
