package com.muromuro.muromuro02.controller;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.MuroMuroSolution;
import com.muromuro.muromuro02.service.MuroMuroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("muromuro_questions")
public class QuestionController {

    private final MuroMuroService service;

    @Autowired
    public QuestionController(MuroMuroService service) {
        this.service = service;
    }

    @GetMapping("list")
    public String listQuestions(Model model) {
        return "questions/list_questions";
    }

    @GetMapping("represent_account_states")
    public String representAccountStates(Model model) {
        MuroMuroSolution muroMuroSolution = new MuroMuroSolution();
        if (muroMuroSolution.getUserInput().isEmpty()) {
            muroMuroSolution.setUserInput(service.getRepresentAccountStatesInitSolution());
        }
        model.addAttribute("muroMuroSolution", muroMuroSolution);

        return "questions/represent_account_states";
    }

    @PostMapping("eval_represent_account_states")
    public String evalRepresentAccountStates(
            @ModelAttribute("muroMuroSolution") MuroMuroSolution muroMuroSolution) {
        MuroMuroResponse response =
                service.evalRepresentAccountStatesSolution(muroMuroSolution.getUserInput());
        muroMuroSolution.setResponse(response);

        return "questions/represent_account_states";
    }
}
