package guideme.authservice.service.token;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Slf4j
public class InternalApiAuthFilter extends OncePerRequestFilter {

    private static final Set<String> ALLOWED_IPS = Set.of("127.0.0.1", "::1"); // 예시

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/internal/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        if (!ALLOWED_IPS.contains(req.getRemoteAddr())) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden internal API");
            return;
        }
        chain.doFilter(req, res);
    }
}