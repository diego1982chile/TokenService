package cl.forevision.jwt.services;

import cl.forevision.jwt.entities.User;
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
public class UserService {

    @PersistenceContext
    private EntityManager entityManager;
    private UserRepository userRepository;

    @PostConstruct
    private void init() {
        // Instantiate Spring Data factory
        RepositoryFactorySupport factory = new JpaRepositoryFactory(entityManager);
        // Get an implemetation of PersonRepository from factory
        this.userRepository = factory.getRepository(UserRepository.class);
    }

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAllOrderByName();
        users.forEach(user -> user.setPassword(null));
        return users;
    }

    @Transactional
    public User saveUser(User user) {
        if(user.isPersisted()) {
            User previous = userRepository.findByUsername(user.getId());
            if(user.getPassword() != null) {
                previous.setPassword(user.getPassword());
            }
            previous.setRoles(user.getRoles());
            /*
            if(retailer.getSchedules() != null) {
                previous.setSchedules(retailer.getSchedules());
            }
            */
            return userRepository.save(previous);
        }
        else {
            return userRepository.save(user);
        }
    }

    @Transactional
    public void deleteUser(String id) {
        userRepository.delete(id);
    }
}
