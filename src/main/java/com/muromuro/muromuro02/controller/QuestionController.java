package com.muromuro.muromuro02.controller;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.MuroMuroSolution;
import com.muromuro.muromuro02.service.evaluator.Evaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The controller for listing the MuroMuro questions, and for evaluating the solutions.
 */
@Controller
@RequestMapping("muromuro_questions")
public class QuestionController {

    private final Evaluator representAccountStates;

    @Autowired
    public QuestionController(
            @Qualifier("representAccountStatesImpl") Evaluator representAccountStates) {
        this.representAccountStates = representAccountStates;
    }

    @GetMapping("list")
    public String listQuestions(Model model) {
        return "questions/list_questions";
    }

    @GetMapping("represent_account_states")
    public String representAccountStates(Model model) {
        MuroMuroSolution muroMuroSolution = new MuroMuroSolution();
        if (muroMuroSolution.getUserInput().isEmpty()) {
            muroMuroSolution.setUserInput(representAccountStates.getInitialSolution());
        }
        model.addAttribute("muroMuroSolution", muroMuroSolution);

        return "questions/represent_account_states";
    }

    @PostMapping("eval_represent_account_states")
    public String evalRepresentAccountStates(
            @ModelAttribute("muroMuroSolution") MuroMuroSolution muroMuroSolution) {
        MuroMuroResponse response =
                representAccountStates.evaluateSolution(muroMuroSolution.getUserInput());
        muroMuroSolution.setResponse(response);

        return "questions/represent_account_states";
    }

    @GetMapping("design_api_with_pagination")
    public String designApiWithPagination(Model model) {
        MuroMuroSolution muroMuroSolution = new MuroMuroSolution();
        // TODO: Populate the initial solution here, as necessary.
        model.addAttribute("muroMuroSolution", muroMuroSolution);
        return "questions/design_api_with_pagination";
    }

    @PostMapping("eval_design_api_with_pagination")
    public String evalDesignApiWithPagination(
            @ModelAttribute("muroMuroSolution") MuroMuroSolution muroMuroSolution) {
        // TODO: Use a proper evaluator here.
        MuroMuroResponse response =
                new MuroMuroResponse(
                        MuroMuroResponse.Status.UNKNOWN,
                        "Evaluator for this solution is not implemented yet.");
        muroMuroSolution.setResponse(response);

        return "questions/design_api_with_pagination";
    }
}
