package com.airSphereConnect.dtos;

public class DepartmentDto {
    // TODO implement ToDto and FromDto methods
    private String name;
    private String code;

    public DepartmentDto() {
    }

    public DepartmentDto(String name, String code) {
        this.name = name;
        this.code = code;
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
