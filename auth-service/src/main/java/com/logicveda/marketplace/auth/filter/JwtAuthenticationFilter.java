package com.logicveda.marketplace.auth.filter;

import com.logicveda.marketplace.auth.service.JwtService;
import com.logicveda.marketplace.auth.repository.UserRepository;
import com.logicveda.marketplace.common.entity.User;
import com.logicveda.marketplace.common.security.JwtUserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * JWT authentication filter that extracts and validates JWT tokens
 * from incoming HTTP requests.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Extract JWT token from request
            String token = extractTokenFromRequest(request);

            if (token != null) {
                // Validate token and extract claims
                Claims claims = jwtService.validateAndGetClaims(token);
                UUID userId = UUID.fromString(claims.get("userId", String.class));

                // Load user from database
                Optional<User> userOpt = userRepository.findById(userId);

                if (userOpt.isPresent()) {
                    User user = userOpt.get();

                    // Create authentication token
                    JwtUserPrincipal principal = JwtUserPrincipal.create(
                            user.getId(),
                            user.getEmail(),
                            user.getEmail(),
                            user.getPasswordHash(),
                            user.getRole().toString(),
                            user.getIsActive()
                    );

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    principal,
                                    token,
                                    principal.getAuthorities()
                            );

                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("JWT authentication successful for user: {}", userId);
                }
            }
        } catch (JwtException e) {
            log.debug("JWT validation failed: {}", e.getMessage());
            // Continue without authentication - will be handled by @PreAuthorize
        } catch (Exception e) {
            log.error("Error processing JWT authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header.
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }

        return null;
    }

    /**
     * Skip JWT filter for specific paths.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/") ||
               path.startsWith("/api/products/public") ||
               path.startsWith("/api/search") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/actuator/health");
    }
}
