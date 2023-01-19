package cl.forevision.jwt.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by root on 12-10-22.
 */
@ApplicationScoped
public class JPAProducer {

    @PersistenceContext
    @Produces
    private EntityManager em;
}
