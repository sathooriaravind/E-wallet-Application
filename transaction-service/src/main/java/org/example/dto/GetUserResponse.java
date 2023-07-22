package org.example.dto;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetUserResponse {

    private String name;

    private String phone; // phone number here

    private String password;

    private String authorities;

    private String email;

    private int age;

    private Date createdOn;

    private Date updatedOn;
}