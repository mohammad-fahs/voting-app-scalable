package com.devops.votingapp.repository;

import com.devops.votingapp.model.VotingOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotingOptionRepository extends JpaRepository<VotingOption, String> {
}
