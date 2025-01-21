package com.muromuro.muromuro02.controller;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.MuroMuroSolution;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.evaluator.Evaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * The controller for listing the MuroMuro questions, and for evaluating the solutions.
 */
@Controller
@RequestMapping("muromuro_questions")
public class QuestionController {

    private final Evaluator representAccountStates;
    private final Evaluator designApiWithPagination;
    private final Evaluator refactorTooManyIfs;
    private final Evaluator deviceDatabase;
    private final Evaluator detectSubstrings;
    private final Evaluator debugList;
    private final Evaluator parseCsv;
    private final Evaluator longRunningFunctions;
    private final Evaluator incompatibleInterfaces;
    private final Evaluator duplicateRpcs;

    @Autowired
    public QuestionController(
            @Qualifier("representAccountStatesImpl") Evaluator representAccountStates,
            @Qualifier("designApiWithPaginationImpl") Evaluator designApiWithPagination,
            @Qualifier("refactorTooManyIfsImpl") Evaluator refactorTooManyIfs,
            @Qualifier("deviceDatabaseImpl") Evaluator deviceDatabase,
            @Qualifier("detectSubstringsImpl") Evaluator detectSubstrings,
            @Qualifier("debugListImpl") Evaluator debugList,
            @Qualifier("parseCsvImpl") Evaluator parseCsv,
            @Qualifier("longRunningFunctionsImpl") Evaluator longRunningFunctions,
            @Qualifier("incompatibleInterfacesImpl") Evaluator incompatibleInterfaces,
            @Qualifier("duplicateRpcsImpl") Evaluator duplicateRpcs){
        this.representAccountStates = representAccountStates;
        this.designApiWithPagination = designApiWithPagination;
        this.refactorTooManyIfs = refactorTooManyIfs;
        this.deviceDatabase = deviceDatabase;
        this.detectSubstrings = detectSubstrings;
        this.debugList = debugList;
        this.parseCsv = parseCsv;
        this.longRunningFunctions = longRunningFunctions;
        this.incompatibleInterfaces = incompatibleInterfaces;
        this.duplicateRpcs = duplicateRpcs;
    }

    @GetMapping("list")
    public String listQuestions(Model model) {
        return "questions/list_questions";
    }

    @GetMapping("represent_account_states")
    public String representAccountStates(Model model) {
        MuroMuroSolution muroMuroSolution = new MuroMuroSolution();
        muroMuroSolution.setUserInput(representAccountStates.getInitialSolution());
        model.addAttribute("muroMuroSolution", muroMuroSolution);

        return "questions/represent_account_states";
    }

    @PostMapping("eval_represent_account_states")
    public String evalRepresentAccountStates(
            @ModelAttribute("muroMuroSolution") MuroMuroSolution muroMuroSolution) {
        MuroMuroResponse response =
                representAccountStates.evaluateSolution(muroMuroSolution.getUserInput());
        muroMuroSolution.setResponse(response);

        return getFragmentForResponseStatus(response.getStatus());
    }

    @GetMapping("design_api_with_pagination")
    public String designApiWithPagination(Model model) {
        MuroMuroSolution muroMuroSolution = new MuroMuroSolution();
        muroMuroSolution.setUserInput(designApiWithPagination.getInitialSolution());
        model.addAttribute("muroMuroSolution", muroMuroSolution);
        return "questions/design_api_with_pagination";
    }

    @PostMapping("eval_design_api_with_pagination")
    public String evalDesignApiWithPagination(
            @ModelAttribute("muroMuroSolution") MuroMuroSolution muroMuroSolution) {
        MuroMuroResponse response =
                designApiWithPagination.evaluateSolution(muroMuroSolution.getUserInput());
        muroMuroSolution.setResponse(response);

        return getFragmentForResponseStatus(response.getStatus());
    }

    @GetMapping("refactor_too_many_ifs")
    public String refactorTooManyIfs(Model model) {
        MuroMuroSolution muroMuroSolution = new MuroMuroSolution();
        muroMuroSolution.setUserInput(refactorTooManyIfs.getInitialSolution());
        model.addAttribute("muroMuroSolution", muroMuroSolution);
        return "questions/refactor_too_many_ifs";
    }

    @PostMapping("eval_refactor_too_many_ifs")
    public String evalRefactorTooManyIfs(
            @ModelAttribute("muroMuroSolution") MuroMuroSolution muroMuroSolution) {
        MuroMuroResponse response =
                refactorTooManyIfs.evaluateSolution(muroMuroSolution.getUserInput());
        muroMuroSolution.setResponse(response);

        return getFragmentForResponseStatus(response.getStatus());
    }

    @GetMapping("device_database")
    public String deviceDatabase(Model model) {
        MuroMuroSolution muroMuroSolution = new MuroMuroSolution();
        muroMuroSolution.setUserInput(deviceDatabase.getInitialSolution());
        model.addAttribute("muroMuroSolution", muroMuroSolution);
        return "questions/device_database";
    }

    @PostMapping("eval_device_database")
    public String evalDeviceDatabase(
            @ModelAttribute("muroMuroSolution") MuroMuroSolution muroMuroSolution) {
        MuroMuroResponse response =
                deviceDatabase.evaluateSolution(muroMuroSolution.getUserInput());
        muroMuroSolution.setResponse(response);

        return getFragmentForResponseStatus(response.getStatus());
    }

    @GetMapping("detect_substrings")
    public String detectSubstrings(Model model) {
        MuroMuroSolution muroMuroSolution = new MuroMuroSolution();
        muroMuroSolution.setUserInput(detectSubstrings.getInitialSolution());
        model.addAttribute("muroMuroSolution", muroMuroSolution);
        return "questions/detect_substrings";
    }

    @PostMapping("eval_detect_substrings")
    public String evalDetectSubstrings(
            @ModelAttribute("muroMuroSolution") MuroMuroSolution muroMuroSolution) {
        MuroMuroResponse response =
                detectSubstrings.evaluateSolution(muroMuroSolution.getUserInput());
        muroMuroSolution.setResponse(response);
        return getFragmentForResponseStatus(response.getStatus());
    }

    @GetMapping("debug_list")
    public String debugList(Model model) {
        MuroMuroSolution muroMuroSolution = new MuroMuroSolution();
        muroMuroSolution.setUserInput(debugList.getInitialSolution());
        model.addAttribute("muroMuroSolution", muroMuroSolution);
        return "questions/debug_list";
    }

    @PostMapping("eval_debug_list")
    public String evalDebugList(
            @ModelAttribute("muroMuroSolution") MuroMuroSolution muroMuroSolution) {
        MuroMuroResponse response =
                debugList.evaluateSolution(muroMuroSolution.getUserInput());
        muroMuroSolution.setResponse(response);
        return getFragmentForResponseStatus(response.getStatus());
    }

    @GetMapping("parse_csv")
    public String parseCsv(Model model) {
        String view = "questions/parse_csv";
        return getPage(model, view, parseCsv);
    }

    @PostMapping("eval_parse_csv")
    public String evalParseCsv(
            @RequestParam("main-def-input") String mainDefInput,
            @ModelAttribute("muroMuroSolution") MuroMuroSolution muroMuroSolution) {
        return evalSolution(mainDefInput, muroMuroSolution, parseCsv);
    }

    @GetMapping("long_running_functions")
    public String longRunningFunctions(Model model) {
        String view = "questions/long_running_functions";
        return getPage(model, view, longRunningFunctions);
    }

    @PostMapping("eval_long_running_functions")
    public String evalLongRunningFunctions(
            @RequestParam("main-def-input") String mainDefInput,
            @ModelAttribute("muroMuroSolution") MuroMuroSolution muroMuroSolution) {
        return evalSolution(mainDefInput, muroMuroSolution, longRunningFunctions);
    }

    @GetMapping("incompatible_interfaces")
    public String incompatibleInterfaces(Model model) {
        String view = "questions/incompatible_interfaces";
        return getPage(model, view, incompatibleInterfaces);
    }

    @PostMapping("eval_incompatible_interfaces")
    public String evalIncompatibleInterfaces(
            @RequestParam("main-def-input") String mainDefInput,
            @ModelAttribute("muroMuroSolution") MuroMuroSolution muroMuroSolution) {
        return evalSolution(mainDefInput, muroMuroSolution, incompatibleInterfaces);
    }

    @GetMapping("duplicate_rpcs")
    public String duplicateRpcs(Model model) {
        String view = "questions/duplicate_rpcs";
        return getPage(model, view, duplicateRpcs);
    }

    @PostMapping("eval_duplicate_rpcs")
    public String evalDuplicateRpcs(
            @RequestParam("caller-code-input") String callerCodeInput,
            @RequestParam("main-def-input") String mainDefInput,
            @ModelAttribute("muroMuroSolution") MuroMuroSolution muroMuroSolution) {
        return evalSolution(
                callerCodeInput, mainDefInput, muroMuroSolution, duplicateRpcs);
    }

    private String getPage(Model model, String view, Evaluator evaluator) {
        MuroMuroSolution muroMuroSolution = new MuroMuroSolution();
        muroMuroSolution.setUserInput(evaluator.getInitialSolution());
        model.addAttribute("muroMuroSolution", muroMuroSolution);
        return view;
    }

    private String evalSolution(
            String callerCodeInput,
            String mainDefInput,
            MuroMuroSolution muroMuroSolution,
            Evaluator evaluator) {
        UserInput userInput = new UserInput(callerCodeInput, mainDefInput);
        return evalSolution(userInput, muroMuroSolution, evaluator);
    }

    private String evalSolution(
            String mainDefInput, MuroMuroSolution muroMuroSolution, Evaluator evaluator) {
        UserInput userInput = new UserInput("", mainDefInput);
        return evalSolution(userInput, muroMuroSolution, evaluator);
    }

    private String evalSolution(
            UserInput userInput, MuroMuroSolution muroMuroSolution, Evaluator evaluator) {
        MuroMuroResponse response = evaluator.evaluateSolution(userInput);
        muroMuroSolution.setResponse(response);
        return getFragmentForResponseStatus(response.getStatus());
    }

    /** Returns a Thymeleaf fragment for the given Muromuro response status code. */
    private String getFragmentForResponseStatus(MuroMuroResponse.Status status) {
        return switch (status) {
            case SUCCESS -> "response :: success";
            case FAILURE -> "response :: failure";
            case TIMEOUT -> "response :: timeout";
            default -> "response :: unknown";
        };
    }
}
