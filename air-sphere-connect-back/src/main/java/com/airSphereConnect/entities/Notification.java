package com.airSphereConnect.entities;

import com.airSphereConnect.entities.enums.NotificationType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "is_checked", nullable = false)
    private boolean isChecked = false;

    @ManyToMany(mappedBy = "notifications")
    private List<User> users = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;


    public Notification() {}

    public Notification(String message, NotificationType notificationType, LocalDateTime sentAt) {
        this.message = message;
        this.notificationType = notificationType;
        this.sentAt = sentAt;
    }
    public Notification(String message, NotificationType notificationType, LocalDateTime sentAt, City city) {
        this.message = message;
        this.notificationType = notificationType;
        this.sentAt = sentAt;
        this.city = city;
    }

    public Notification(String message, NotificationType notificationType, LocalDateTime sentAt, Department department) {
        this.message = message;
        this.notificationType = notificationType;
        this.sentAt = sentAt;
        this.department = department;
    }

    public Notification(String message, NotificationType notificationType, LocalDateTime sentAt, Region region) {
        this.message = message;
        this.notificationType = notificationType;
        this.sentAt = sentAt;
        this.region = region;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Notification{" +
                "message='" + message + '\'' +
                ", notificationType=" + notificationType +
                '}';
    }
}
