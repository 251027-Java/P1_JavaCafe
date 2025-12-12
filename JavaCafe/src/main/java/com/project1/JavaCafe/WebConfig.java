package com.project1.JavaCafe;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // Fields
    private final JwtInterceptor jwtInterceptor;

    // Constructor
    public WebConfig(JwtInterceptor jwti) {
        this.jwtInterceptor = jwti;
    }

    @Override
    public void addInterceptors(InterceptorRegistry reg) {
        // adding interceptors to the list of active/running interceptors
        // that are scanning requests as they come in
        reg.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**") // Apply to all /api/ paths

                // EXCLUSION: Allow login without a token
                .excludePathPatterns("/api/auth/login", "/api/auth/register")

                // EXCLUSION: Allow the menu page without a token
                .excludePathPatterns("/api/menu", "/api/menu/**", "api/contact"); // Excludes all methods (GET, POST, etc.) for this path
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Apply CORS rules to all paths starting with /api
                .allowedOrigins("http://localhost:5174") // <-- **CRITICAL LINE**
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow all necessary HTTP methods
                .allowedHeaders("*") // Allow all request headers
                .allowCredentials(true);
    }
}
