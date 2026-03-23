package com.airsphereconnect.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entité représentant une station de mesure de la qualité de l'air.
 * Une station enregistre les mesures de différents polluants atmosphériques à un emplacement géographique donné.
 */
@Entity
@Table(name = "air_quality_stations")
public class AirQualityStation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Identifiant unique de la station.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom de la station de mesure.
     */
    @NotBlank(message = "{station.name.required}")
    @Size(max = 100, message = "{station.name.size}")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * Code unique identifiant la station.
     */
    @NotBlank(message = "{station.code.required}")
    @Size(max = 20, message = "{station.code.size}")
    @Column(name = "code", unique = true, nullable = false, length = 20)
    private String code;

    /**
     * Code de la zone géographique où se situe la station.
     */
    @Size(max = 10, message = "{station.areaCode.size}")
    @Column(name = "area_code", length = 10)
    private String areaCode;

    /**
     * Code INSEE de la commune où se situe la station.
     */
    @Size(max = 10, message = "{station.inseeCode.size}")
    @Column(name = "insee_code", length = 10)
    private String inseeCode;

    /**
     * Ville où est située la station de mesure.
     */
    @NotNull(message = "{station.city.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    /**
     * Liste des mesures effectuées par cette station.
     */
    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AirQualityMeasurement> measurements = new ArrayList<>();

    /**
     * Constructeur par défaut.
     */
    public AirQualityStation() {}

    /**
     * Constructeur avec tous les paramètres.
     *
     * @param name       Le nom de la station
     * @param code       Le code unique de la station
     * @param areaCode   Le code de la zone géographique
     * @param inseeCode  Le code INSEE de la commune
     * @param city       La ville où est située la station
     */
    public AirQualityStation(String name, String code, String areaCode, String inseeCode, City city) {
        this.name = name;
        this.code = code;
        this.areaCode = areaCode;
        this.inseeCode = inseeCode;
        this.city = city;
    }

    /**
     * Retourne l'identifiant unique de la station.
     *
     * @return L'identifiant unique
     */
    public Long getId() {
        return id;
    }

    /**
     * Définit l'identifiant unique de la station.
     *
     * @param id L'identifiant unique
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retourne le nom de la station.
     *
     * @return Le nom de la station
     */
    public String getName() {
        return name;
    }

    /**
     * Définit le nom de la station.
     *
     * @param name Le nom de la station
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retourne le code unique de la station.
     *
     * @return Le code de la station
     */
    public String getCode() {
        return code;
    }

    /**
     * Définit le code unique de la station.
     *
     * @param code Le code de la station
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Retourne le code de la zone géographique.
     *
     * @return Le code de la zone
     */
    public String getAreaCode() {
        return areaCode;
    }

    /**
     * Définit le code de la zone géographique.
     *
     * @param areaCode Le code de la zone
     */
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    /**
     * Retourne le code INSEE de la commune.
     *
     * @return Le code INSEE
     */
    public String getInseeCode() {
        return inseeCode;
    }

    /**
     * Définit le code INSEE de la commune.
     *
     * @param inseeCode Le code INSEE
     */
    public void setInseeCode(String inseeCode) {
        this.inseeCode = inseeCode;
    }

    /**
     * Retourne la ville où est située la station.
     *
     * @return La ville
     */
    public City getCity() {
        return city;
    }

    /**
     * Définit la ville où est située la station.
     *
     * @param city La ville
     */
    public void setCity(City city) {
        this.city = city;
    }

    /**
     * Retourne la liste des mesures effectuées par cette station.
     *
     * @return La liste des mesures
     */
    public List<AirQualityMeasurement> getMeasurements() {
        return measurements;
    }

    /**
     * Définit la liste des mesures effectuées par cette station.
     *
     * @param measurements La liste des mesures
     */
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
