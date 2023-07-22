package org.example.service;

import org.example.config.Constants;
import org.example.dto.GetUserResponse;
import org.example.model.User;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Service
public class AuthenticationService implements UserDetailsService {

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /**
         * Since user related info is not present in transaction DB, we won't follow general approach
         * of calling repository like before
         */
        HttpHeaders httpHeaders = new HttpHeaders();

        /**
         * Different services have dummy user authentication details with some authorities
         * to talk to other services and perform some actions
         */
        //httpHeaders.setBasicAuth("txn-service", "txn@123");
        String app_username = Constants.this_service_username;
        String password = Constants.this_service_password;
        String plainCredentials = app_username + ":" + password;
        String base64Credentials = Base64.getEncoder().encodeToString(plainCredentials.getBytes());

        httpHeaders.add("Authorization", "Basic " + base64Credentials);

        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<GetUserResponse> responseEntity = restTemplate.exchange("http://localhost:9000/user/phone/" +
                username, HttpMethod.GET, requestEntity, GetUserResponse.class);

        GetUserResponse responseBody = responseEntity.getBody();

        return User.builder()
                .username(responseBody.getPhone())
                .password(responseBody.getPassword())
                .authorities(responseBody.getAuthorities())
                .build();
    }
}
