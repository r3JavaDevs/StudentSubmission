package com.submission.controller;

import com.submission.dto.GradeRequest;
import com.submission.model.Result;
import com.submission.service.ResultService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/results")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ResultController extends BaseController {

    @Inject
    private ResultService resultService;

    @POST
    @Path("/submissions/{submissionId}/grade")
    public Response gradeSubmission(
            @PathParam("submissionId") Long submissionId,
            GradeRequest gradeRequest) {
        try {
            Long teacherId = getCurrentTeacherId();
            Result result = resultService.gradeSubmission(
                submissionId,
                teacherId,
                gradeRequest.getGrade(),
                gradeRequest.getRemarks()
            );
            return ok("Submission graded successfully", result);
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        } catch (Exception e) {
            return serverError("Failed to grade submission: " + e.getMessage());
        }
    }

    @GET
    @Path("/assignments/{assignmentId}")
    public Response getResultsByAssignment(@PathParam("assignmentId") Long assignmentId) {
        try {
            Long teacherId = getCurrentTeacherId();
            List<Result> results = resultService.getResultsByAssignment(assignmentId, teacherId);
            return ok("Results retrieved successfully", results);
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        } catch (Exception e) {
            return serverError("Failed to retrieve results: " + e.getMessage());
        }
    }

    @GET
    @Path("/submissions/{submissionId}")
    public Response getResult(@PathParam("submissionId") Long submissionId) {
        try {
            Long teacherId = getCurrentTeacherId();
            return resultService.getResult(submissionId, teacherId)
                .map(result -> ok("Result retrieved successfully", result))
                .orElse(notFound("Result not found"));
        } catch (Exception e) {
            return serverError("Failed to retrieve result: " + e.getMessage());
        }
    }

    @PUT
    @Path("/submissions/{submissionId}/grade")
    public Response updateGrade(
            @PathParam("submissionId") Long submissionId,
            GradeRequest gradeRequest) {
        try {
            Long teacherId = getCurrentTeacherId();
            Result result = resultService.updateGrade(
                submissionId,
                teacherId,
                gradeRequest.getGrade(),
                gradeRequest.getRemarks()
            );
            return ok("Grade updated successfully", result);
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        } catch (Exception e) {
            return serverError("Failed to update grade: " + e.getMessage());
        }
    }

    @DELETE
    @Path("/submissions/{submissionId}/grade")
    public Response deleteGrade(@PathParam("submissionId") Long submissionId) {
        try {
            Long teacherId = getCurrentTeacherId();
            resultService.deleteGrade(submissionId, teacherId);
            return ok("Grade deleted successfully");
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        } catch (Exception e) {
            return serverError("Failed to delete grade: " + e.getMessage());
        }
    }
} 