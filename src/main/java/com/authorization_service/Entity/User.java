package com.authorization_service.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
@NoArgsConstructor @AllArgsConstructor @Getter @Setter @ToString
public class User {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    @Column(name = "created_at")
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

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Role role;

    @ToString.Exclude
    @JsonManagedReference
    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL)
    private List<AccessToken> sessions = new ArrayList<>();

    @ToString.Exclude
    @JsonManagedReference
    @OneToMany(mappedBy = "user",
               cascade = CascadeType.ALL)
    private List<AccessToken> accessTokens = new ArrayList<>();
}

