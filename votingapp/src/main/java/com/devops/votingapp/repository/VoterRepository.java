package com.devops.votingapp.repository;

import com.devops.votingapp.model.Voter;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface VoterRepository extends JpaRepository<Voter, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Voter> findByNationalId(String nationalId);

    long countByHasVotedTrue();

    long countByHasVotedFalse();


}