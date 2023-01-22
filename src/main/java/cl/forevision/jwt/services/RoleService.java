package cl.forevision.jwt.services;

import cl.forevision.jwt.entities.Role;
import cl.forevision.jwt.entities.User;
import cl.forevision.jwt.repositories.RoleRepository;
import cl.forevision.jwt.repositories.UserRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by root on 13-10-22.
 */
@RequestScoped
public class RoleService {

    @PersistenceContext
    private EntityManager entityManager;
    private RoleRepository roleRepository;

    @PostConstruct
    private void init() {
        // Instantiate Spring Data factory
        RepositoryFactorySupport factory = new JpaRepositoryFactory(entityManager);
        // Get an implemetation of PersonRepository from factory
        this.roleRepository = factory.getRepository(RoleRepository.class);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAllOrderByName();
    }

    @Transactional
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(Role role) {
        Role previous = roleRepository.findByRolename(role.getPreviousRolename());
        roleRepository.delete(previous);
        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(String id) {
        roleRepository.delete(id);
    }
}
