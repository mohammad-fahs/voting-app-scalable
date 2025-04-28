package com.devops.votingapp.service;

import com.devops.votingapp.model.VotingOption;
import com.devops.votingapp.repository.VotingOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VotingOptionService {

    private final VotingOptionRepository optionRepo;


    public List<VotingOption> initializeOptions(List<VotingOption> options) {
        return optionRepo.saveAll(options);
    }

    public List<VotingOption> getAllOptions() {
        return optionRepo.findAll();
    }
}

