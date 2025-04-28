package com.devops.votingapp.controller;

import com.devops.votingapp.model.Voter;
import com.devops.votingapp.model.VotingOption;
import com.devops.votingapp.service.VoterService;
import com.devops.votingapp.service.VotingOptionService;
import com.devops.votingapp.service.VotingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UIController {
    private final VotingOptionService votingOptionService;
    private final  VotingService votingService;
    private final VoterService voterService;


    @GetMapping("/voting-options")
    public String showVotingOptions(Model model) {
        List<VotingOption> options = votingOptionService.getAllOptions();
        model.addAttribute("options", options);
        return "voting-options";
    }

    @GetMapping("/display-voters")
    public String listVoters(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "15") int size,
                             Model model) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Voter> voters = voterService.getAllVoters(pageRequest);
        model.addAttribute("voters", voters);
        return "voters";
    }

    @GetMapping("/voting-results")
    public String showVotingResults(Model model) {
        model.addAttribute("results", votingService.getResults());
        return "voting-results";
    }
}
