package com.authorization_service.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "scopes")
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class Scope {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;


    @Column(name = "scope", length = 200)
    String name;

    public Scope(String name) {
        this.name = name;
    }

    @JsonManagedReference
    @ManyToMany(mappedBy = "scopes")
    private Set<Role> roles = new HashSet<>();

    @JsonManagedReference
    @ManyToMany(mappedBy = "scopes")
    private Set<AccessToken> accessTokens = new HashSet<>();

    @JsonManagedReference
    @ManyToMany(mappedBy = "scopes")
    private Set<Session> sessions = new HashSet<>();

    @Override
    public String toString() {
        return "Scope{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", roles=" + roles +
                ", accessTokens=" + accessTokens +
                ", sessions=" + sessions +
                '}';
    }
}
