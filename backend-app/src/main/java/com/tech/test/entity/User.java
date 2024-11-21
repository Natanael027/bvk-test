package com.tech.test.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tech.test.model.UserDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 128, nullable = false, unique = true)
    private String email;

    @Column(length = 64, nullable = false)
    private String password;

    @Column(length = 64, nullable = false)
    private String position;

    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String photos;

    private boolean enabled;

    @Transient
    private boolean hasChildren;

    @ManyToOne()
    @JoinColumn(name = "report_to", unique = false)
    @JsonBackReference
    private User parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("name asc")
    @JsonManagedReference
    private Set<User> children = new HashSet<>();

    @Column(name = "all_parent_ids", length = 256, nullable = true)
    private String allParentIDs;

    public User(Integer id) {
        this.id = id;
    }

    public User(String name, User parent) {
        this.name = name;
        this.parent = parent;
    }

    public User(String name, String email, String password, String position, boolean enabled, User parent, String photoUrl) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.position = position;
        this.enabled = enabled;
        this.parent = parent;
        this.photos = photoUrl;
    }

    public void addChildren(User user){
        this.children.add(user);
    }

    public void updateAllParentIDs() {
        if (parent == null) {
            // If there is no parent, we set allParentIDs to null or empty
            this.allParentIDs = null;
        } else {
            // If there's a parent, inherit their allParentIDs and prepend the current parent's ID
            this.allParentIDs = parent.getAllParentIDs() != null ? parent.getAllParentIDs() + this.parent.getId() + "-" : "-" + this.parent.getId() + "-";
        }

        // Ensure that children also have updated allParentIDs
        for (User child : children) {
            child.updateAllParentIDs(); // Recursively update for all children
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", enabled=" + enabled +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    public UserDTO toDTO() {
        String photoUrl = this.photos;
        Integer parentId = (parent != null) ? parent.getId() : null;

        return new UserDTO(
                this.id,
                this.email,
                this.password,
                this.position,
                this.name,
                photoUrl,
                this.enabled,
                parentId
        );
    }
}
