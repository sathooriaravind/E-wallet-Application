package org.example.dtos;

import lombok.*;
import org.example.models.User;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter@Setter@Builder@NoArgsConstructor@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank
    private String name;

    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    private String password;

    @Min(18)
    private int age;

    public User toUser(){
        return User.builder()
                .name(this.name)
                .email(this.email)
                .phone(this.phone)
                .password(this.password)
                .age(this.age)
                .build();
    }



}
