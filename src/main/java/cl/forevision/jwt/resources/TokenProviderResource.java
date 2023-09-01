package cl.forevision.jwt.resources;

import cl.forevision.jwt.services.CypherService;
import cl.forevision.jwt.entities.Role;
import cl.forevision.jwt.entities.User;
import cl.forevision.jwt.repositories.RoleRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

/**
 * Created by root on 09-12-22.
 */
@ApplicationScoped
@Path("/auth")
@Log4j
public class TokenProviderResource {

    @Inject
    CypherService cypherService;

    @PersistenceContext
    private EntityManager entityManager;

    private RoleRepository roleRepository;

    private PrivateKey key;

    private Role ADMIN, USER;


    @Context
    SecurityContext securityContext;


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
            log.error(e.getMessage());
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
            log.error(ex.getMessage());

            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        String token = CypherService.generateJWT(key, user.getUsername(), target);

        Map<String, String> jwt = new HashMap<>();
        jwt.put("token", token);

        return Response.status(Response.Status.OK)
                .header(AUTHORIZATION, "Bearer ".concat(token))
                .entity(jwt)
                .build();

    }

}
