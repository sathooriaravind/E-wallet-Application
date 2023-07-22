package org.example.dtos;

import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUserResponse {

    private String name;

    private String password;

    private String authorities;

    private String email;

    private String phone;

    private int age;

}
