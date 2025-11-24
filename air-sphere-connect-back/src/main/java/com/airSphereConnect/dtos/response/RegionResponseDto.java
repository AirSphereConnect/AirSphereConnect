package com.airSphereConnect.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Données envoyées de la région")
public class RegionResponseDto {

    @Schema(description = "Identifiant de la région", example = "1")
    private Long id;

    @Schema(description = "Nom de la région", example = "Occitanie")
    private String name;

    @Schema(description = "Code du Département", example = "11")
    private String code;

    public RegionResponseDto() {
    }

    public RegionResponseDto(Long id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
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
}
