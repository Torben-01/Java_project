package com.example.demo.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import java.time.LocalDate;

@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String salutation;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate date;
    
    @Column(length = 1000) 
    private String comment;

    private String modul; 

    public Request() {}

    public Request(String salutation, String firstName, String lastName, String email, LocalDate date, String comment, String modul) {
        this.salutation = salutation;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.date = date;
        this.comment = comment;
        this.modul = modul;
    }

    public Long getId() { return id; }
    public String getsalutation() { return salutation; }
    public String getfirstName() { return firstName; }
    public String getlastName() { return lastName; }
    public String getEmail() { return email; }
    public LocalDate getdate() { return date; }
    public String getcomment() { return comment; }
    public String getModul() { return modul; }

    public void setsalutation(String salutation) { this.salutation = salutation; }
    public void setfirstName(String firstName) { this.firstName = firstName; }
    public void setlastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setdate(LocalDate date) { this.date = date; }
    public void setcomment(String comment) { this.comment = comment; }
    public void setModul(String modul) { this.modul = modul; }
}
