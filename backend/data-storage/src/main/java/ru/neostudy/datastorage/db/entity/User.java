package ru.neostudy.datastorage.db.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User implements Serializable {
    public enum eRole {
        visitor, external_user, candidate
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID userId;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private eRole role;

    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "phone_number")
    private String phoneNumber;
}