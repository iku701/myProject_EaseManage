package edu.du.myproject1101_1.entity;

import javax.persistence.*; // javax 패키지로 변경
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user")
@Setter
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String company;

    private String address;

    private String city;

    private String country;

    private String postalCode;

    @Lob // 대용량 텍스트 저장 가능
    private String aboutMe;

    @Column(nullable = false)
    private String role = "USER"; // 기본 역할 설정
}
