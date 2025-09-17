package com.airSphereConnect.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "populations")
public class Population {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "population", nullable = false)
    private Integer population;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "density", nullable = false)
    private Double density;

    @Column(name = "source", nullable = false, length = 100)
    private String source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;


    public Population() {
    }

    public Population(Integer population, Integer year, Double density, String source, City city) {
        this.population = population;
        this.year = year;
        this.density = density;
        this.source = source;
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getDensity() {
        return density;
    }

    public void setDensity(Double density) {
        this.density = density;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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
        Population that = (Population) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Population{" +
                "population=" + population +
                ", year=" + year +
                ", density=" + density +
                ", source='" + source + '\'' +
                '}';
    }
}
