package com.authorization_service.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "access_tokens")
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class AccessToken {

    @Id
    @Column(name = "access_token", length = 60, updatable = false)
    private String access_token;

    @Column(name = "token_type", nullable = false)
    private String token_type;
    @Column(name = "refresh_token")
    private String refresh_token;

    @Column(name = "id_token")
    private String id_token;

    @Column(name = "expires_in", nullable = false)
    private int expires_in;

    @Column(name = "scope")
    private String scope;

    @Column(name = "created_at")
    private LocalDateTime created_at = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User user;

    public AccessToken(String access_token,
                       String token_type,
                       String refresh_token,
                       String id_token,
                       int expires_in,
                       String scope) {
        this.access_token = access_token;
        this.token_type = token_type;
        this.refresh_token = refresh_token;
        this.id_token = id_token;
        this.expires_in = expires_in;
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "AccessToken{" +
                "access_token='" + access_token + '\'' +
                ", token_type='" + token_type + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                ", id_token='" + id_token + '\'' +
                ", expires_in=" + expires_in +
                ", scope='" + scope + '\'' +
                ", created_at=" + created_at +
                '}';
    }
}
