package com.onlinejudge.controller;

import com.onlinejudge.dto.AdminImportRequest;
import com.onlinejudge.dto.AdminImportResponse;
import com.onlinejudge.service.ProblemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminImportController {

    private final ProblemService problemService;

    @PostMapping("/import")
    public ResponseEntity<AdminImportResponse> importProblems(@Valid @RequestBody AdminImportRequest request) {
        return ResponseEntity.ok(problemService.importProblems(request));
    }
}