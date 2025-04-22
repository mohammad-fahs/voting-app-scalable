package com.devops.votingapp.repository;

import com.devops.votingapp.model.Vote;
import com.devops.votingapp.model.VotingOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, String> {
    long countByVotingOption(VotingOption option);
}