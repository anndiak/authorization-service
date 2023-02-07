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
@Table(name = "redirect_uris")
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class RedirectUri {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    @Column(name = "name", unique = true)
    private String name;

    @JsonManagedReference
    @ManyToMany(mappedBy = "redirectUris")
    private Set<Application> applications = new HashSet<>();

    @Override
    public String toString() {
        return "RedirectUri{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
