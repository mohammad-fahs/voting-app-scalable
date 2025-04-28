package com.devops.votingapp.service;

import com.devops.votingapp.model.Voter;
import com.devops.votingapp.repository.VoterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoterService {
    private final VoterRepository voterRepo;
    private final MetricsService metricsService;

    public void importVoters(List<Voter> voters) {
        for(Voter voter : voters){
            voterRepo.save(voter);
            log.info("Voter created {} with NID {}",voter.getName(),voter.getNationalId());
           metricsService.updateVoterMetrics();
        }

    }

    public List<Voter> getAllVoters() {
        return voterRepo.findAll();
    }

    public Page<Voter> getAllVoters(Pageable pageable) {
        return voterRepo.findAll(pageable);
    }

    public Optional<Voter> getVoter(String id) {
        return voterRepo.findByNationalId(id);
    }

    public Optional<Voter> getVoterByNationalId(String nationalId) {
        return voterRepo.findByNationalId(nationalId);
    }

    public void markVoted(Voter voter) {
        voter.setHasVoted(true);
        voterRepo.save(voter);
    }
}
