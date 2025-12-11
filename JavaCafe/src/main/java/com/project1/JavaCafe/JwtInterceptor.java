package com.project1.JavaCafe;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    // Fields
    private final JwtUtil jwtUtil;

    // Constructor
    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // Methods
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1. Authentication Check (Token Presence)
        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Missing or invalid token format.");
            return false;
        }

        String token = authHeader.substring(7);

        // 2. Authentication Check (Token Validity)
        if (!jwtUtil.validateToken(token)){
            // Token is invalid/expired
            // TEMPORARY LOGGING:
            System.out.println("--- TOKEN VALIDATION FAILED FOR: " + token);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: invalid token.");
            return false;
        }

        // --- AUTHORIZATION AND CONTEXT SETTING ---

        // Extract required data from the now-validated token
        String userEmail = jwtUtil.getEmailFromToken(token);
        Long userId = jwtUtil.getUserIdFromToken(token);
        String userRole = jwtUtil.getRoleFromToken(token);
        String requestUri = request.getRequestURI();

        // 3. Manual Authorization Logic (Centralized Role Check)

        // Rule: RESTRICT /api/admin/** to ROLE_ADMIN only.
        if (requestUri.startsWith("/api/admin")) {
            if (!"ADMIN".equals(userRole)) {
                // Deny access: User is authenticated but not authorized for this resource
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Forbidden: Administrator access required.");
                return false;
            }
        }

        // Rule: Optional check for /api/orders (requires any authenticated user with a valid role)
        // Since the token is valid, this check is primarily to ensure the role is either
        // ADMIN or CUSTOMER, which should be redundant if your tokens are always correct.
        if (requestUri.startsWith("/api/orders")) {
            if (!"ADMIN".equals(userRole) && !"CUSTOMER".equals(userRole)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Forbidden: Valid customer or admin role required.");
                return false;
            }
        }

        // 4. Context Setting
        // Attach the user details to the request for the controller to use if needed
        request.setAttribute("userId", userId);
        //request.setAttribute("email", userEmail);
        request.setAttribute("userRole", userRole);

        return true; // Request is authenticated and authorized, proceed to controller
    }
}