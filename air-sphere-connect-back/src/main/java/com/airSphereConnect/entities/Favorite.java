package com.airSphereConnect.entities;

import com.airSphereConnect.entities.enums.FavoriteCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "favorites")
public class Favorite extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "{favorite.category.required}")
    @Enumerated(EnumType.STRING)
    @Column(name = "favorite_category", nullable = false)
    private FavoriteCategory favoriteCategory;

    @NotNull(message = "{favorite.user.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "{favorite.city.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    public Favorite() {}

    public Favorite(FavoriteCategory favoriteCategory, User user, City city) {
        this.favoriteCategory = favoriteCategory;
        this.user = user;
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FavoriteCategory getFavoriteCategory() {
        return favoriteCategory;
    }

    public void setFavoriteCategory(FavoriteCategory favoriteCategory) {
        this.favoriteCategory = favoriteCategory;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Favorite favorite = (Favorite) o;
        return Objects.equals(id, favorite.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Favorite{" +
                "favoriteCategory=" + favoriteCategory +
                '}';
    }
}
