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


    public void initializeOptions(List<VotingOption> options) {
        optionRepo.saveAll(options);
    }

    public List<VotingOption> getAllOptions() {
        return optionRepo.findAll();
    }
}

