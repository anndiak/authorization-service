package com.authorization_service.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class User {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime created_at = LocalDateTime.now();

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updated_at = LocalDateTime.now();

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private String role;

    @JsonManagedReference
    @OneToMany(mappedBy = "user",
               cascade = CascadeType.ALL)
    private List<AccessToken> accessTokens = new ArrayList<>();


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    public List<AccessToken> getAccessTokens() {
        return accessTokens;
    }

    public void setAccessTokens(List<AccessToken> accessTokens) {
        this.accessTokens = accessTokens;

        for(AccessToken token : accessTokens) {
            token.setUser(this);
        }
    }

    public void addAccessToken(AccessToken accessToken) {
        accessTokens.add(accessToken);
        accessToken.setUser(this);
    }
}

