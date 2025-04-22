package com.devops.votingapp.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@Entity
@Table(name = "voters", uniqueConstraints = @UniqueConstraint(columnNames = "nationalId"))
public class Voter implements Serializable {

    @Id
    private String id;

    private String name;

    @Column(nullable = false, unique = true)
    private String nationalId;

    private boolean hasVoted;

    public Voter() {
        this.id = UUID.randomUUID().toString();
        this.hasVoted = false;
    }

    public Voter(String name, String nationalId) {
        this();
        this.name = name;
        this.nationalId = nationalId;
    }
}