package com.airSphereConnect.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Entity
@Table(name = "populations")
public class Population {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "{population.population.required}")
    @Column(name = "population", nullable = false)
    private Integer population;

    @NotNull(message = "{population.year.required}")
    @Column(name = "`year`", nullable = false)
    private Integer year;

    @NotBlank(message = "{population.source.required}")
    @Size(max = 100, message = "{population.source.size}")
    @Column(name = "source", nullable = false, length = 100)
    private String source;

    @NotNull(message = "{population.city.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;


    public Population() {
    }

    public Population(Integer population, Integer year, String source, City city) {
        this.population = population;
        this.year = year;
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
                ", source='" + source + '\'' +
                '}';
    }
}
