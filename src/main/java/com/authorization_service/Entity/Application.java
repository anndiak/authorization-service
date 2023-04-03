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

    @Column(name = "client_secret", nullable = false)
    private String client_secret;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "homepage_url")
    private String homepage_url;

    @Column(name = "description")
    private String description;

    @Column(name = "scope")
    private String scope;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime created_at = LocalDateTime.now();

    @JsonManagedReference
    @OneToMany(mappedBy = "application",
            cascade = CascadeType.ALL)
    private List<Session> sessions = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "applications_redirect_uris",
            joinColumns = { @JoinColumn(name = "client_id") },
            inverseJoinColumns = { @JoinColumn(name = "redirect_uri_id") }
    )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<RedirectUri> redirectUris = new HashSet<RedirectUri>();

    @ManyToMany
    @JoinTable(
            name = "applications_grant_types",
            joinColumns = { @JoinColumn(name = "client_id") },
            inverseJoinColumns = { @JoinColumn(name = "grant_type_id") }
    )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<GrantType> grantTypes = new HashSet<GrantType>();

    @Override
    public String toString() {
        return "Application{" +
                "client_id=" + client_id +
                ", client_secret='" + client_secret + '\'' +
                ", name='" + name + '\'' +
                ", homepage_url='" + homepage_url + '\'' +
                ", description='" + description + '\'' +
                ", scope='" + scope + '\'' +
                ", created_at=" + created_at +
                '}';
    }
}
