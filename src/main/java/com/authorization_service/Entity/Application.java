package com.authorization_service.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class Application {

    @Id
    @Column(length = 36, updatable = false)
    private String client_id;

    @Column(name = "client_secret", nullable = false)
    private String client_secret;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "homepage_url")
    private String homepage_url;

    @Column(name = "description")
    private String description;

    @Column(name = "redirect_uri", nullable = false)
    private String redirect_uri;

    @Column(name = "scope")
    private String scope;

    @Column(name = "created_at")
    private LocalDateTime created_at = LocalDateTime.now();

//    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
//    @JoinTable(name="assigned_applications_to_users",
//            joinColumns={
//                    @JoinColumn(name="client_id", referencedColumnName="client_id")},
//            inverseJoinColumns={
//                    @JoinColumn(name="user_id", referencedColumnName="id")})
//    private User user;

    @Override
    public String toString() {
        return "Application{" +
                "client_id=" + client_id +
                ", client_secret='" + client_secret + '\'' +
                ", name='" + name + '\'' +
                ", homepage_url='" + homepage_url + '\'' +
                ", description='" + description + '\'' +
                ", redirect_uri='" + redirect_uri + '\'' +
                ", scope='" + scope + '\'' +
                ", created_at=" + created_at +
                '}';
    }
}
