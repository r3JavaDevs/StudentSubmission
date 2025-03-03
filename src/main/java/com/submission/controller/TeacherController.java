package com.submission.controller;

import com.submission.dto.LoginRequest;
import com.submission.dto.TeacherDTO;
import com.submission.model.Teacher;
import com.submission.service.TeacherService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/api/teacher")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeacherController extends BaseController {

    @Inject
    private TeacherService teacherService;

    @POST
    @Path("/login")
    public Response login(LoginRequest loginRequest) {
        try {
            return teacherService.authenticateTeacher(loginRequest.getEmail(), loginRequest.getPassword())
                .map(teacher -> ok("Login successful", toDTO(teacher)))
                .orElse(unauthorized("Invalid email or password"));
        } catch (Exception e) {
            return serverError("Login failed: " + e.getMessage());
        }
    }

    @POST
    @Path("/register")
    public Response register(TeacherDTO teacherDTO) {
        try {
            if (teacherService.isEmailAvailable(teacherDTO.getEmail())) {
                Teacher teacher = teacherService.registerTeacher(
                    teacherDTO.getFirstName(),
                    teacherDTO.getLastName(),
                    teacherDTO.getEmail(),
                    teacherDTO.getPassword()
                );
                return ok("Registration successful", toDTO(teacher));
            } else {
                return error("Email already exists");
            }
        } catch (Exception e) {
            return serverError("Registration failed: " + e.getMessage());
        }
    }

    @GET
    @Path("/profile")
    public Response getProfile() {
        try {
            Long teacherId = getCurrentTeacherId();
            return teacherService.findTeacherById(teacherId)
                .map(teacher -> ok("Profile retrieved successfully", toDTO(teacher)))
                .orElse(notFound("Teacher not found"));
        } catch (Exception e) {
            return serverError("Failed to retrieve profile: " + e.getMessage());
        }
    }

    @PUT
    @Path("/profile")
    public Response updateProfile(TeacherDTO teacherDTO) {
        try {
            Long teacherId = getCurrentTeacherId();
            Teacher teacher = teacherService.findTeacherById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
            teacher.setFirstName(teacherDTO.getFirstName());
            teacher.setLastName(teacherDTO.getLastName());
            Teacher updatedTeacher = teacherService.updateTeacher(teacher);
            return ok("Profile updated successfully", toDTO(updatedTeacher));
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        } catch (Exception e) {
            return serverError("Failed to update profile: " + e.getMessage());
        }
    }

    @PUT
    @Path("/password")
    public Response changePassword(@QueryParam("currentPassword") String currentPassword,
                                 @QueryParam("newPassword") String newPassword) {
        try {
            Long teacherId = getCurrentTeacherId();
            teacherService.updatePassword(teacherId, currentPassword, newPassword);
            return ok("Password changed successfully");
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        } catch (Exception e) {
            return serverError("Failed to change password: " + e.getMessage());
        }
    }

    @GET
    @Path("/list")
    public Response getAllTeachers() {
        try {
            List<TeacherDTO> teachers = teacherService.getAllTeachers().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
            return ok("Teachers retrieved successfully", teachers);
        } catch (Exception e) {
            return serverError("Failed to retrieve teachers: " + e.getMessage());
        }
    }

    private TeacherDTO toDTO(Teacher teacher) {
        return new TeacherDTO(
            teacher.getId(),
            teacher.getFirstName(),
            teacher.getLastName(),
            teacher.getEmail()
        );
    }
} 