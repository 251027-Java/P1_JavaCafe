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
        // is the auth header attached/included?
        String authHeader = request.getHeader("Authorization");

        // is the token included?
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // is the token valid?
        String token = authHeader.substring(7);
        if (jwtUtil.validateToken(token)){
            //ADDED
            String userEmail = jwtUtil.getEmailFromToken(token);
            request.setAttribute("email", userEmail);
            return true;
        }

        // if not, reject the request
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Unauthorized: invalid token");
        return false;
    }
}
