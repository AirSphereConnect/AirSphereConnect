package com.airSphereConnect.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "air_quality_stations")
public class AirQualityStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{station.name.required}")
    @Size(max = 100, message = "{station.name.size}")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "{station.code.required}")
    @Size(max = 20, message = "{station.code.size}")
    @Column(name = "code", unique = true, nullable = false, length = 20)
    private String code;

    @Size(max = 10, message = "{station.areaCode.size}")
    @Column(name = "area_code", length = 10)
    private String areaCode;

    @NotNull(message = "{station.city.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insee_code", referencedColumnName = "insee_code", nullable = false)
    private City city;

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AirQualityMeasurement> measurements = new ArrayList<>();

    public AirQualityStation() {}

    public AirQualityStation(String name, String code, String areaCode, City city) {
        this.name = name;
        this.code = code;
        this.areaCode = areaCode;
        this.city = city;
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

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<AirQualityMeasurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<AirQualityMeasurement> measurements) {
        this.measurements = measurements;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AirQualityStation that = (AirQualityStation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AirQualityStation{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
