package cl.forevision.jwt.resources;

import cl.forevision.jwt.entities.Role;
import cl.forevision.jwt.entities.User;
import cl.forevision.jwt.repositories.RoleRepository;
import cl.forevision.jwt.services.CypherService;
import lombok.extern.log4j.Log4j;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

/**
 * Created by root on 09-12-22.
 */
@RequestScoped
@Path("healthcheck")
public class HealthcheckResource implements HealthCheck {

    @GET
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("Application started").up().build();
    }


}
