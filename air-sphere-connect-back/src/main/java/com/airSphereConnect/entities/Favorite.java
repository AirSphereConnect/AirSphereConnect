package com.airSphereConnect.entities;

import com.airSphereConnect.entities.enums.FavoriteCategory;
import com.jayway.jsonpath.JsonPath;
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

//    @NotNull(message = "{favorite.category.required}")
//    @Enumerated(EnumType.STRING)
//    @Column(name = "favorite_category", nullable = false)
//    private FavoriteCategory favoriteCategory;

    @NotNull(message = "{alert.enabled.required}")
    @Column(name = "weather", nullable = false)
    private boolean selectWeather;

    @NotNull(message = "{alert.enabled.required}")
    @Column(name = "air_quality", nullable = false)
    private boolean selectAirQuality;

    @NotNull(message = "{alert.enabled.required}")
    @Column(name = "population", nullable = false)
    private boolean selectPopulation;

    @NotNull(message = "{favorite.user.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "{favorite.city.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    public Favorite() {}

    public Favorite(User user, City city) {
        this.user = user;
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public FavoriteCategory getFavoriteCategory() {
//        return favoriteCategory;
//    }
//
//    public void setFavoriteCategory(FavoriteCategory favoriteCategory) {
//        this.favoriteCategory = favoriteCategory;
//    }

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

    public boolean getSelectPopulation() {
        return selectPopulation;
    }

    public void setSelectPopulation(boolean selectPopulation) {
        this.selectPopulation = selectPopulation;
    }


    public boolean getSelectAirQuality() {
        return this.selectAirQuality;
    }

    public void setSelectAirQuality(boolean selectAirQuality) {
        this.selectAirQuality = selectAirQuality;
    }

    public boolean getSelectWeather() {
        return selectWeather;
    }

    public void setSelectWeather(boolean selectWeather) {
        this.selectWeather = selectWeather;
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
                "favoriteCategory=" + selectAirQuality + selectPopulation + selectWeather +
                '}';
    }

}
