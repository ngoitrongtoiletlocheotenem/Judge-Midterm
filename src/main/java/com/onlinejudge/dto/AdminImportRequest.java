package com.onlinejudge.dto;

import com.onlinejudge.model.Problem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AdminImportRequest {

    private boolean clearExisting = false;

    @NotEmpty(message = "At least one problem is required")
    @Valid
    private List<ProblemImportItem> problems;

    @Data
    public static class ProblemImportItem {
        @NotBlank(message = "Problem title is required")
        private String title;

        @NotBlank(message = "Problem description is required")
        private String description;

        @NotNull(message = "Problem difficulty is required")
        private Problem.Difficulty difficulty;

        @NotNull(message = "Time limit is required")
        private Integer timeLimit;

        @NotNull(message = "Memory limit is required")
        private Integer memoryLimit;

        @NotEmpty(message = "At least one test case is required")
        @Valid
        private List<TestCaseImportItem> testCases;
    }

    @Data
    public static class TestCaseImportItem {
        @NotBlank(message = "Test case input is required")
        private String input;

        @NotBlank(message = "Expected output is required")
        private String expectedOutput;

        private Boolean isHidden = false;

        private Integer orderIndex = 0;
    }
}