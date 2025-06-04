package com.smileShark.entity.school.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class SchoolLoginRequest {
    private String username;
    private String password;
    private Integer code = 2341;
    private String client_id;
    private String client_secret;
    private String grant_type = "password";
    private Integer tenant_id = 32;

    public SchoolLoginRequest(String username, String password, String client_id, String client_secret) {
        this.username = username;
        this.password = password;
        this.client_id = client_id;
        this.client_secret = client_secret;
    }

}
