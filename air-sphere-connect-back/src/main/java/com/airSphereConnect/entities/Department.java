package com.airSphereConnect.entities;

import jakarta.persistence.*;
import com.airSphereConnect.entities.Region;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "departments")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{department.name.required}")
    @Size(max = 100, message = "{department.name.size}")
    @Column(name= "name", nullable = false)
    private String name;

    @NotBlank(message = "{department.code.required}")
    @Size(max = 20, message = "{department.code.size}")
    @Column(name= "code", nullable = false, unique = true)
    private String code;

    @NotNull(message = "{department.region.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="region_id", nullable = false)
    private Region region;

    @OneToMany(mappedBy = "department")
    private List<City> cities;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Alerts> alerts;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FavoritesAlerts> favoritesAlerts;

    public Department() {}

    public Department(String name, String code, Region region) {
        this.name = name;
        this.code = code;
        this.region = region;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public List<Alerts> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alerts> alerts) {
        this.alerts = alerts;
    }

    public List<FavoritesAlerts> getFavoritesAlerts() {
        return favoritesAlerts;
    }

    public void setFavoritesAlerts(List<FavoritesAlerts> favoritesAlerts) {
        this.favoritesAlerts = favoritesAlerts;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", regionId=" + (region != null ? region.getId() : null) +
                '}';
    }
}
