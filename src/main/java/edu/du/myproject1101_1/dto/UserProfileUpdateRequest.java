package edu.du.myproject1101_1.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UserProfileUpdateRequest {
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String company;
    private String address;
    private String city;
    private String country;
    private String postalCode;
    private String aboutMe;
}