package com.authorization_service.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "temporary_sessions")
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class Session {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "state")
    private String state;

    @Column(name = "code", unique = true)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Application application;

    @Column(name = "code_challenge")
    private String code_challenge;

    @Column(name = "code_challenge_method")
    private String code_challenge_method;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime created_at = LocalDateTime.now();

    @Column(name = "expires_at")
    private LocalDateTime expires_at = LocalDateTime.now();

    @Override
    public String toString() {
        return "Session{" +
                "state='" + state + '\'' +
                ", code='" + code + '\'' +
                ", application=" + application +
                ", code_challenge='" + code_challenge + '\'' +
                ", code_challenge_method='" + code_challenge_method + '\'' +
                ", created_at=" + created_at +
                ", expires_at=" + expires_at +
                '}';
    }
}
