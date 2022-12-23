package cl.dsoto.jwt.controller;

import cl.dsoto.jwt.auth.CypherService;
import cl.dsoto.jwt.auth.RolesEnum;
import cl.dsoto.jwt.entities.Role;
import cl.dsoto.jwt.entities.User;
import cl.dsoto.jwt.repositories.RoleRepository;
import cl.dsoto.jwt.repositories.UserRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

/**
 * Created by root on 09-12-22.
 */
@Singleton
@Path("/auth")
public class TokenProviderResource {

    @Inject
    CypherService cypherService;

    @PersistenceContext
    private EntityManager entityManager;

    private RoleRepository roleRepository;

    private PrivateKey key;

    private Role ADMIN, USER;


    @PostConstruct
    public void init() {
        try {
            // Instantiate Spring Data factory
            RepositoryFactorySupport factory = new JpaRepositoryFactory(entityManager);

            key = cypherService.readPrivateKey();

            this.roleRepository = factory.getRepository(RoleRepository.class);
            List<Role> roles = roleRepository.findAll();
            ADMIN = roles.stream().filter(e -> e.getRolename().equals("ADMIN")).findAny().get();
            USER = roles.stream().filter(e -> e.getRolename().equals("USER")).findAny().get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    //@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response doPosLogin(User user, @Context HttpServletRequest request) {

        List<String> target = new ArrayList<>();

        try {
            request.login(user.getUsername(), user.getPassword());

            if (request.isUserInRole(ADMIN.getRolename()))
                target.add(ADMIN.getRolename());

            if (request.isUserInRole(USER.getRolename()))
                target.add(USER.getRolename());

        } catch (ServletException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        String token = cypherService.generateJWT(key, user.getUsername(), target);

        Map<String, String> jwt = new HashMap<>();
        jwt.put("token", token);

        return Response.status(Response.Status.OK)
                .header(AUTHORIZATION, "Bearer ".concat(token))
                .entity(jwt)
                .build();

    }

}
