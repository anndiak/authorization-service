package com.authorization_service.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roles")
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class Role {

    @Id
    @Column(name = "role", length = 200)
    String name;

    @JsonManagedReference
    @OneToMany(mappedBy = "role",
            cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();

    @Override
    public String toString() {
        return "Role{" +
                "name='" + name + '\'' +
                '}';
    }
}
