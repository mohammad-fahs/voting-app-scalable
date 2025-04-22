package com.devops.votingapp.service;

import com.devops.votingapp.model.Vote;
import com.devops.votingapp.model.Voter;
import com.devops.votingapp.model.VotingOption;
import com.devops.votingapp.repository.VoteRepository;
import com.devops.votingapp.repository.VoterRepository;
import com.devops.votingapp.repository.VotingOptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VotingService {

    private final VoteRepository voteRepo;
    private final VoterRepository voterRepo;
    private final VotingOptionRepository optionRepo;
    private final MetricsService metrics;


    @Transactional
    public void vote(String optionId, String nationalId) {
        metrics.incrementVoteRequest(); // record vote attempt

        Voter voter = voterRepo.findByNationalId(nationalId)
                .orElseThrow(() -> {
                    metrics.incrementVoteFailure();
                    return new RuntimeException("Voter not found");
                });

        if (voter.isHasVoted()) {
            metrics.incrementVoteFailure();
            throw new RuntimeException("Voter has already voted");
        }

        VotingOption option = optionRepo.findById(optionId)
                .orElseThrow(() -> {
                    metrics.incrementVoteFailure();
                    return new RuntimeException("Option not found");
                });

        Vote vote = new Vote(voter, option);
        voteRepo.save(vote);

        voter.setHasVoted(true);
        voterRepo.save(voter);

        updateAllMetrics();
        log.info("voter {} voted",voter.getName());
    }

    @Transactional
    public void clearVotesAndVoters() {
        voteRepo.deleteAll();
        voterRepo.deleteAll();
        metrics.updateVoterMetrics();
        metrics.updateVoteResults();
        log.info("All votes and voters have been cleared.");
    }


    public Map<String, Long> getResults() {
        return optionRepo.findAll().stream()
                .collect(Collectors.toMap(
                        VotingOption::getName,
                        voteRepo::countByVotingOption
                ));
    }

    public void updateAllMetrics() {
        long total = voterRepo.count();
        long voted = voterRepo.findAll().stream().filter(Voter::isHasVoted).count();
        long notVoted = total - voted;
        long options = optionRepo.count();

        metrics.updateVoterMetrics();
        metrics.updateVotingOptionsMetric();

        metrics.updateVoteResults();
    }
}
