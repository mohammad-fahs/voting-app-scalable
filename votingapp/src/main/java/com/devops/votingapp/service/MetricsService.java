package com.devops.votingapp.service;

import com.devops.votingapp.model.VotingOption;
import com.devops.votingapp.repository.VoteRepository;
import com.devops.votingapp.repository.VoterRepository;
import com.devops.votingapp.repository.VotingOptionRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsService {

    private final MeterRegistry registry;
    private final VotingOptionRepository votingOptionRepository;
    private final VoterRepository voterRepository;
    private final VoteRepository voteRepository;

    @Getter
    private volatile long totalVoters;
    @Getter
    private volatile long votedVoters;
    @Getter
    private volatile long notVotedVoters;
    @Getter
    private volatile long totalVotingOptions;

    private final Map<String, AtomicLong> optionVoteCounts = new ConcurrentHashMap<>();

    @PostConstruct
    public void initMetrics() {
        registry.gauge("voters", this, MetricsService::getTotalVoters);
        registry.gauge("voters_voted", this, MetricsService::getVotedVoters);
        registry.gauge("voters_not_voted", this, MetricsService::getNotVotedVoters);
        registry.gauge("voting_options", this, MetricsService::getTotalVotingOptions);

        updateVoterMetrics();
        updateVotingOptionsMetric();
        updateVoteResults();
        log.info("Initial metrics populated.");
    }

    public void updateVoteResults() {
        Map<String, Long> results = votingOptionRepository.findAll().stream()
                .collect(Collectors.toMap(
                        VotingOption::getName,
                        voteRepository::countByVotingOption
                ));

        results.forEach((option, count) -> {
            AtomicLong atomicCount = optionVoteCounts.computeIfAbsent(option, opt -> {
                Tags tags = Tags.of("option", opt);
                AtomicLong newCounter = new AtomicLong(count);
                registry.gauge("votes_per_option", tags, newCounter);
                return newCounter;
            });

            atomicCount.set(count); // update the value
            //log.info("votes option {} count {}", option, count);
        });
    }


    public void incrementVoteRequest() {
        registry.counter("voting_requests_total").increment();
    }

    public void incrementVoteFailure() {
        registry.counter("vote_failures_total").increment();
    }

    public void updateVoterMetrics() {
        totalVoters = voterRepository.count();
        votedVoters = voterRepository.countByHasVotedTrue();
        notVotedVoters = voterRepository.countByHasVotedFalse();
        //log.info("Voter metrics updated: total={}, voted={}, not voted={}", totalVoters, votedVoters, notVotedVoters);
    }

    public void updateVotingOptionsMetric() {
        totalVotingOptions = votingOptionRepository.count();
        //log.info("Voting options metrics updated: total={}", totalVotingOptions);
    }

}
