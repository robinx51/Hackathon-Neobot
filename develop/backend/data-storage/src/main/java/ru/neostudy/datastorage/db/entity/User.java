package ru.neostudy.datastorage.db.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements Serializable {
    public enum eRole {
        visitor, external_user, candidate
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", columnDefinition = "int", updatable = false, nullable = false)
    private int userId;

    @Column(name = "telegram_id")
    private String telegramId;

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