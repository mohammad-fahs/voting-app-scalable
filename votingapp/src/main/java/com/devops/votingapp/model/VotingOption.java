package com.devops.votingapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "voting_options")
public class VotingOption {

    @Id
    private String id;

    private String name;

    public VotingOption() {
        this.id = UUID.randomUUID().toString();
    }

    public VotingOption(String name) {
        this();
        this.name = name;
    }
}
