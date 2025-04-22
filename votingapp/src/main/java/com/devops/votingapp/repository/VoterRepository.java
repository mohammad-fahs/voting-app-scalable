package com.devops.votingapp.repository;

import com.devops.votingapp.model.Voter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoterRepository extends JpaRepository<Voter, String> {
    Optional<Voter> findByNationalId(String nationalId);

    long countByHasVotedTrue();

    long countByHasVotedFalse();


}