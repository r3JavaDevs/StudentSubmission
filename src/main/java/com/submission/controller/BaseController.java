package com.submission.controller;

import com.submission.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.security.Principal;

public abstract class BaseController {
    
    @Context
    protected HttpServletRequest request;
    
    @Context
    protected SecurityContext securityContext;
    
    protected Long getCurrentTeacherId() {
        Principal principal = securityContext.getUserPrincipal();
        if (principal == null) {
            throw new SecurityException("No authenticated user found");
        }
        return Long.parseLong(principal.getName());
    }
    
    protected Response ok(String message) {
        return Response.ok(ApiResponse.success(message)).build();
    }
    
    protected <T> Response ok(String message, T data) {
        return Response.ok(ApiResponse.success(message, data)).build();
    }
    
    protected Response error(String message) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse.error(message))
                .build();
    }
    
    protected Response unauthorized(String message) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(ApiResponse.error(message))
                .build();
    }
    
    protected Response forbidden(String message) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity(ApiResponse.error(message))
                .build();
    }
    
    protected Response notFound(String message) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(ApiResponse.error(message))
                .build();
    }
    
    protected Response serverError(String message) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.error(message))
                .build();
    }
} 