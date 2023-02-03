package com.authorization_service.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "applications")
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class Application {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "client_id", updatable = false)
    private String client_id;

    @Column(name = "client_secret")
    private String client_secret;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "homepage_url")
    private String homepage_url;

    @Column(name = "description")
    private String description;

    @Column(name = "redirect_uri")
    private String redirect_uri;

    @Column(name = "scope")
    private String scope;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime created_at = LocalDateTime.now();

    @JsonManagedReference
    @OneToMany(mappedBy = "application",
            cascade = CascadeType.ALL)
    private List<Session> sessions = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "assigned_applications_to_users",
            joinColumns = { @JoinColumn(name = "client_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") }
    )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<User> users = new HashSet<User>();

    public void addUser(User user) {
        this.users.add(user);
        user.getApplications().add(this);
    }

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
