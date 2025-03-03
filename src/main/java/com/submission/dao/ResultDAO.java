package com.submission.dao;

import com.submission.model.Result;
import com.submission.model.ResultStatus;
import java.util.List;
import java.util.Optional;

public interface ResultDAO extends BaseDAO<Result> {
    List<Result> findByAssignmentId(Long assignmentId);
    List<Result> findByStudentId(Long studentId);
    Optional<Result> findByAssignmentAndStudentId(Long assignmentId, Long studentId);
    List<Result> findByStatus(ResultStatus status);
    List<Result> findAppealedResults();
    void updateGrade(Long resultId, String grade, String remarks);
    void updateAppealStatus(Long resultId, ResultStatus status, String remarks);
} 