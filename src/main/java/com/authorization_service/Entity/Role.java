package com.authorization_service.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "roles")
@NoArgsConstructor @AllArgsConstructor @Getter @Setter @ToString
public class Role {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    @Column(name = "role", unique = true)
    private String role;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "assigned_scopes_to_roles",
            joinColumns = { @JoinColumn(name = "role_id") },
            inverseJoinColumns = { @JoinColumn(name = "scope_id") }
    )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<Scope> scopes = new HashSet<>();

    @ToString.Exclude
    @JsonManagedReference
    @OneToMany(mappedBy = "role",
            cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();

}
