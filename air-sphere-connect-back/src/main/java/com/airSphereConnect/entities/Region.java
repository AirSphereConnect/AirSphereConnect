package com.airSphereConnect.entities;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "regions")
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name",  unique = true, nullable = false, length = 50)
    private String name;

    @Column(name="code",  unique = true, nullable = false, length = 10)
    private String code;


    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Department> departments = new ArrayList<>();

    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications = new ArrayList<>();


    public  Region() {}

    public Region(String name, String code) {
       this.name = name;
        this.code = code;

    }

    public void addDepartment(Department department) {
        if(department != null) {
            this.departments.add(department);
            department.setRegion(this);
        }
    }

    public void removeDepartment(Department department) {
        if(department != null) {
            this.departments.remove(department);
            department.setRegion(null);
        }
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

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Region region = (Region) obj;
        return Objects.equals(code, region.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "Region{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
