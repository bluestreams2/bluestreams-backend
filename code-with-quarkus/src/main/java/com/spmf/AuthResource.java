package com.spmf;

import com.spmf.dto.LoginRequest;
import com.spmf.dto.RegisterRequest;

import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.mindrot.jbcrypt.BCrypt;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @POST
    @Path("/register")
    @Transactional
    public Response register(
            RegisterRequest request
    ) {

        User existingUser =
                User.find(
                        "username",
                        request.username
                ).firstResult();

        if (existingUser != null) {

            return Response.status(
                    Response.Status.CONFLICT
            ).entity(
                    "Username already exists"
            ).build();
        }

        User existingEmail =
                User.find(
                        "email",
                        request.email
                ).firstResult();

        if (existingEmail != null) {

            return Response.status(
                    Response.Status.CONFLICT
            ).entity(
                    "Email already exists"
            ).build();
        }

        User user = new User();

        user.username =
                request.username;

        user.email =
                request.email;

        user.passwordHash =
                BCrypt.hashpw(
                        request.password,
                        BCrypt.gensalt()
                );

        user.role = "USER";

        user.enabled = true;

        user.persist();

        return Response.ok(
                "User created"
        ).build();
    }

    @POST
    @Path("/login")
    public Response login(
            LoginRequest request
    ) {

        User user =
                User.find(
                        "username",
                        request.username
                ).firstResult();

        if (user == null) {

            return Response.status(
                    Response.Status.UNAUTHORIZED
            ).build();
        }

        boolean valid =
                BCrypt.checkpw(
                        request.password,
                        user.passwordHash
                );

        if (!valid) {

            return Response.status(
                    Response.Status.UNAUTHORIZED
            ).build();
        }

        String token =
                JwtUtil.generateToken(
                        user.id,
                        user.username,
                        user.role
                );

        return Response.ok(
                token
        ).build();
    }

    @GET
    @Path("/check")
    @Authenticated
    public Response check() {
        return Response.ok().build();
    }

}