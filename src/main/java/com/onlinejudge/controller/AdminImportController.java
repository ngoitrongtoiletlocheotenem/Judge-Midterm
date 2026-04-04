package com.onlinejudge.controller;

import com.onlinejudge.dto.AdminImportRequest;
import com.onlinejudge.dto.AdminImportResponse;
import com.onlinejudge.service.ProblemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminImportController {

    private final ProblemService problemService;

    @GetMapping("/import")
    public ResponseEntity<?> importHelp() {
        return ResponseEntity.ok(
                java.util.Map.of(
                        "message", "Use POST /api/admin/import with a JSON body to import problems.",
                        "example", "curl -X POST ..."
                )
        );
    }

    @PostMapping("/import")
    public ResponseEntity<AdminImportResponse> importProblems(@Valid @RequestBody AdminImportRequest request) {
        return ResponseEntity.ok(problemService.importProblems(request));
    }
}