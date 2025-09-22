package com.airSphereConnect.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApiDepartmentResponseDto (
    @JsonProperty("code") String code,
    @JsonProperty("nom") String name,
    @JsonProperty("codeRegion") String regionCode
){}