package com.cursojava.todolist.task.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cursojava.todolist.user.repository.IUserRepository;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var path = request.getServletPath();
        if (path.startsWith("/tasks/")){
            var authorization = request.getHeader("Authorization");
            var encoded = authorization.substring("Basic".length()).trim();
            byte[] decoded = Base64.getDecoder().decode(encoded);
            String authString = new String(decoded);
            String[] credentials = authString.split(":");

            String username = credentials[0];
            String password = credentials[1];

            var user = userRepository.findByUsername(username);
            if (user == null) {
                response.sendError(HttpStatus.UNAUTHORIZED.value());
            } else {
                var passwordVerified = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passwordVerified.verified){
                    request.setAttribute("idUser", user.getId());
                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(HttpStatus.UNAUTHORIZED.value());
                }
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
