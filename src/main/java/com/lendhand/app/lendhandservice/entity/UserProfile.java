package com.lendhand.app.lendhandservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
public class UserProfile {
    @Id
    private Long id;

    @Column(length = 100)
    private String location;

    @Column(length = 500)
    private String about;

    @Column(length = 255)
    private String avatarUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    public UserProfile(User user) {
        this.user = user;
    }
}
