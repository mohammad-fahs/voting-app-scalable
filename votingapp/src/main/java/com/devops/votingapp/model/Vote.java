package com.devops.votingapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "votes")
public class Vote {

    @Id
    private String id;

    @ManyToOne
    private Voter voter;

    @ManyToOne
    private VotingOption votingOption;

    private LocalDateTime timestamp;

    public Vote() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }

    public Vote(Voter voter, VotingOption option) {
        this();
        this.voter = voter;
        this.votingOption = option;
    }
}
