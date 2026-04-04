package com.onlinejudge.service;

import com.onlinejudge.dto.AdminImportRequest;
import com.onlinejudge.dto.AdminImportResponse;
import com.onlinejudge.dto.ProblemResponse;
import com.onlinejudge.model.Problem;
import com.onlinejudge.model.TestCase;
import com.onlinejudge.repository.ProblemRepository;
import com.onlinejudge.repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;

    public List<ProblemResponse> getAllProblems() {
        return problemRepository.findAllByOrderByIdAsc()
                .stream()
                .map(p -> ProblemResponse.from(p, List.of()))
                .toList();
    }

    public ProblemResponse getProblemById(Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Problem not found: " + id));
        
        List<TestCase> visibleTestCases = testCaseRepository
                .findByProblemIdAndIsHiddenFalseOrderByOrderIndexAsc(id);
        
        return ProblemResponse.from(problem, visibleTestCases);
    }

    public Problem createProblem(Problem problem) {
        return problemRepository.save(problem);
    }

    public TestCase addTestCase(Long problemId, TestCase testCase) {
        problemRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("Problem not found: " + problemId));
        testCase.setProblemId(problemId);
        return testCaseRepository.save(testCase);
    }

    @Transactional
    public AdminImportResponse importProblems(AdminImportRequest request) {
        if (request.isClearExisting()) {
            testCaseRepository.deleteAllInBatch();
            problemRepository.deleteAllInBatch();
        }

        int problemCount = 0;
        int testCaseCount = 0;

        for (AdminImportRequest.ProblemImportItem item : request.getProblems()) {
            Problem savedProblem = problemRepository.save(Problem.builder()
                    .title(item.getTitle())
                    .description(item.getDescription())
                    .difficulty(item.getDifficulty())
                    .timeLimit(item.getTimeLimit())
                    .memoryLimit(item.getMemoryLimit())
                    .build());

            problemCount++;

            for (AdminImportRequest.TestCaseImportItem testCaseItem : item.getTestCases()) {
                testCaseRepository.save(TestCase.builder()
                        .problemId(savedProblem.getId())
                        .input(testCaseItem.getInput())
                        .expectedOutput(testCaseItem.getExpectedOutput())
                        .isHidden(testCaseItem.getIsHidden())
                        .orderIndex(testCaseItem.getOrderIndex())
                        .build());
                testCaseCount++;
            }
        }

        return AdminImportResponse.builder()
                .problemsImported(problemCount)
                .testCasesImported(testCaseCount)
                .clearedExisting(request.isClearExisting())
                .build();
    }
}
