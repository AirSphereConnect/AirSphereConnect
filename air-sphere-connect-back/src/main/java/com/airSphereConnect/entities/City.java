package com.airSphereConnect.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cities")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "insee_code", unique = true, nullable = false, length = 10)
    private String inseeCode;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "postal_code", length = 10, nullable = true)
    private String postalCode;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "area_code", length = 10)
    private String areaCode;

    @Column(name= "population")
    private Integer population;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Population> populations = new ArrayList<>();

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AirQualityStation> airQualityStations = new ArrayList<>();

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WeatherMeasurement> weatherMeasurements = new ArrayList<>();

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Favorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Alerts> alerts = new ArrayList<>();

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FavoritesAlerts> favoritesAlerts = new ArrayList<>();


    public City() {}

    public City(String name, String inseeCode, String postalCode, Double latitude, Double longitude, String areaCode,
                Department department, Integer population) {
        this.inseeCode = inseeCode;
        this.name = name;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.areaCode = areaCode;
        this.department = department;
        this.population = population;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInseeCode() {
        return inseeCode;
    }
    public void setInseeCode(String inseeCode) {
        this.inseeCode = inseeCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public List<Population> getPopulations() {
        return populations;
    }

    public void setPopulations(List<Population> populations) {
        this.populations = populations;
    }

    public List<AirQualityStation> getAirQualityStations() {
        return airQualityStations;
    }

    public void setAirQualityStations(List<AirQualityStation> airQualityStations) {
        this.airQualityStations = airQualityStations;
    }

    public List<WeatherMeasurement> getWeatherMeasurements() {
        return weatherMeasurements;
    }

    public void setWeatherMeasurements(List<WeatherMeasurement> weatherMeasurements) {
        this.weatherMeasurements = weatherMeasurements;
    }

    public List<Favorite> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Favorite> favorites) {
        this.favorites = favorites;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
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
        City city = (City) o;
        return Objects.equals(name, city.name) && Objects.equals(postalCode, city.postalCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, postalCode);
    }

    @Override
    public String toString() {
        return "City{" +
                "inseeCode='" + inseeCode + '\'' +
                "name='" + name + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", areaCode='" + areaCode + '\'' +
                '}';
    }
}
