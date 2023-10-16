package br.com.rodrigoaraujo.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.rodrigoaraujo.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// classe genérica gerenciável pelo spring
@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {
        var servletPath = request.getServletPath();
        if(servletPath.startsWith("/tasks/")) {
            // Auth user
            var auth = request.getHeader("Authorization");
            // It comes like "Basic <base64auth>"
            var authEncoded = auth.substring("Basic".length()).trim();
            // decode base64
            byte[] authDecoded = Base64.getDecoder().decode(authEncoded);
            var authString = new String(authDecoded);
            // result is username:password
            String[] credentials = authString.split(":");
            String username = credentials[0];
            String password = credentials[1];
    
            // Validate user
            var user = this.userRepository.findByUsername(username);
            if(user == null) {
                response.sendError(401, "User not authorized");
            }
            else {
                // Validate password
                var passVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if(passVerify.verified) { //password is correct
                    request.setAttribute("idUser", user.getId());
                    // done
                    filterChain.doFilter(request, response);
                }
                else {
                    response.sendError(401, "Incorrect Password");
                }
            }
        } else {
            // Is not tasks router
            filterChain.doFilter(request, response);
        }
    }
}
