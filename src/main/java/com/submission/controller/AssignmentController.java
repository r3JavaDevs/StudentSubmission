package com.submission.controller;

import com.submission.dto.AssignmentRequest;
import com.submission.model.Assignment;
import com.submission.service.AssignmentService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/assignments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AssignmentController extends BaseController {

    @Inject
    private AssignmentService assignmentService;

    @POST
    public Response createAssignment(AssignmentRequest request) {
        try {
            Long teacherId = getCurrentTeacherId();
            Assignment assignment = assignmentService.createAssignment(
                request.getTitle(),
                request.getDescription(),
                request.getFilePath(),
                request.getDeadline(),
                teacherId
            );
            return ok("Assignment created successfully", assignment);
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        } catch (Exception e) {
            return serverError("Failed to create assignment: " + e.getMessage());
        }
    }

    @GET
    public Response getTeacherAssignments() {
        try {
            Long teacherId = getCurrentTeacherId();
            List<Assignment> assignments = assignmentService.getTeacherAssignments(teacherId);
            return ok("Assignments retrieved successfully", assignments);
        } catch (Exception e) {
            return serverError("Failed to retrieve assignments: " + e.getMessage());
        }
    }

    @GET
    @Path("/{id}")
    public Response getAssignment(@PathParam("id") Long assignmentId) {
        try {
            return assignmentService.findAssignmentById(assignmentId)
                .map(assignment -> ok("Assignment retrieved successfully", assignment))
                .orElse(notFound("Assignment not found"));
        } catch (Exception e) {
            return serverError("Failed to retrieve assignment: " + e.getMessage());
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateAssignment(@PathParam("id") Long assignmentId, AssignmentRequest request) {
        try {
            Assignment updated = assignmentService.updateAssignment(
                assignmentId,
                request.getTitle(),
                request.getDescription(),
                request.getDeadline()
            );
            return ok("Assignment updated successfully", updated);
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        } catch (Exception e) {
            return serverError("Failed to update assignment: " + e.getMessage());
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteAssignment(@PathParam("id") Long assignmentId) {
        try {
            Long teacherId = getCurrentTeacherId();
            assignmentService.deleteAssignment(assignmentId, teacherId);
            return ok("Assignment deleted successfully");
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        } catch (Exception e) {
            return serverError("Failed to delete assignment: " + e.getMessage());
        }
    }
}