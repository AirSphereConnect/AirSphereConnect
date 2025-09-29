package com.airSphereConnect.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ExternalAlertDto {


    private Long cityId;
    private Long departmentId;
    private Long regionId;

    @NotNull(message = "type est obligatoire")
    @Size(min = 1, message = "type ne peut être vide")
    private String type;

    @NotNull(message = "message est obligatoire")
    @Size(min = 1)
    private String message;

    public ExternalAlertDto() {}

    public ExternalAlertDto(Long cityId, String type, String message, Long departmentId, Long regionId) {
        this.cityId = cityId;
        this.departmentId = departmentId;
        this.regionId = regionId;
        this.type = type;
        this.message = message;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }
}
