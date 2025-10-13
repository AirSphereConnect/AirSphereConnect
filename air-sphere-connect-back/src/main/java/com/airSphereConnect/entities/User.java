package com.airSphereConnect.entities;

import com.airSphereConnect.entities.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name= "users")
public class User extends Timestamp implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{user.username.notBlank}")
    @Size(min = 2, max = 50, message = "{user.username.size}")
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank(message = "{user.email.notBlank}")
    @Email(message = "{user.email.invalid}")
    @Column(name = "email", unique = true, nullable = false, length = 150)
    private String email;

    @NotBlank(message = "{user.password.notBlank}")
    @Size(min = 8, message = "{user.password.min}")
    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "ENUM('USER','ADMIN','GUEST')")
    private UserRole role = UserRole.USER;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Address address;

    @OneToMany(mappedBy = "user",  cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Favorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "user",  cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FavoritesAlerts> favoritesAlerts = new ArrayList<>();


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Alerts> alerts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ForumRubric> forumRubrics = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ForumThread> forumThreads = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ForumPost> forumPosts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostReaction> postReactions = new ArrayList<>();


    public User() {}

    public User(String username, String email, String password, UserRole role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Favorite> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Favorite> favorites) {
        this.favorites = favorites;
    }

    public List<Alerts> getNotifications() {
        return alerts;
    }

    public void setNotifications(List<Alerts> alerts) {
        this.alerts = alerts;
    }

    public List<ForumRubric> getForumRubrics() {
        return forumRubrics;
    }

    public void setForumRubrics(List<ForumRubric> forumRubrics) {
        this.forumRubrics = forumRubrics;
    }

    public List<ForumThread> getForumThreads() {
        return forumThreads;
    }

    public void setForumThreads(List<ForumThread> forumThreads) {
        this.forumThreads = forumThreads;
    }

    public List<ForumPost> getForumPosts() {
        return forumPosts;
    }

    public void setForumPosts(List<ForumPost> forumPosts) {
        this.forumPosts = forumPosts;
    }

    public List<PostReaction> getPostReactions() {
        return postReactions;
    }

    public void setPostReactions(List<PostReaction> postReactions) {
        this.postReactions = postReactions;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    public List<FavoritesAlerts> getFavoritesAlerts() {
        return favoritesAlerts;
    }

    public void setFavoritesAlerts(List<FavoritesAlerts> favoritesAlerts) {
        this.favoritesAlerts = favoritesAlerts;
    }

    public List<Alerts> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alerts> alerts) {
        this.alerts = alerts;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    //Besoin pour la gestion des rôles dans spring sécurity
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

}
