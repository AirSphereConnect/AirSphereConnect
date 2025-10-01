package com.airSphereConnect.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ExternalAlertDto {


    private Long cityId;

    @NotNull(message = "type est obligatoire")
    @Size(min = 1, message = "type ne peut être vide")
    private String type;

    @NotNull(message = "message est obligatoire")
    @Size(min = 1)
    private String message;

    public ExternalAlertDto() {}

    public ExternalAlertDto(Long cityId, String type, String message) {
        this.cityId = cityId;
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
}
