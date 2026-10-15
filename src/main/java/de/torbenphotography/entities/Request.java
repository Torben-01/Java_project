package de.torbenphotography.entities;

import de.torbenphotography.images.PhotoCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
public class Request {

    public static final int NAME_MAX_LENGTH = 100;
    public static final int EMAIL_MAX_LENGTH = 255;
    public static final int COMMENT_MAX_LENGTH = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String salutation;

    @Column(nullable = false, length = NAME_MAX_LENGTH)
    private String firstName;

    @Column(nullable = false, length = NAME_MAX_LENGTH)
    private String lastName;

    @Column(nullable = false, length = EMAIL_MAX_LENGTH)
    private String email;

    @Column(nullable = false)
    private LocalDate date;

    @Column(length = COMMENT_MAX_LENGTH)
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PhotoCategory category;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Request() {}

    public Request(PhotoCategory category) {
        this.category = category;
    }

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getSalutation() { return salutation; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public LocalDate getDate() { return date; }
    public String getComment() { return comment; }
    public PhotoCategory getCategory() { return category; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setSalutation(String salutation) { this.salutation = salutation; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setComment(String comment) { this.comment = comment; }
    public void setCategory(PhotoCategory category) { this.category = category; }
}
