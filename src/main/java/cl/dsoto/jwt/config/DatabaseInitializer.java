package cl.dsoto.jwt.config;

import cl.dsoto.jwt.entities.Role;
import cl.dsoto.jwt.entities.User;
import cl.dsoto.jwt.repositories.RoleRepository;
import cl.dsoto.jwt.repositories.UserRepository;
import cl.dsoto.jwt.util.PasswordUtils;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Created by root on 09-12-22.
 */
@Startup
@Singleton
@Log4j
public class DatabaseInitializer {

    @PersistenceContext
    private EntityManager entityManager;

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    private static final String SCRIPTS_PATH = "/scripts/";

    @Resource(lookup = "java:global/accountsDS")
    private DataSource dataSource;

    @PostConstruct
    private void init() {
        // Instantiate Spring Data factory
        RepositoryFactorySupport factory = new JpaRepositoryFactory(entityManager);
        // Get an implemetation of PersonRepository from factory
        this.userRepository = factory.getRepository(UserRepository.class);
        this.roleRepository = factory.getRepository(RoleRepository.class);

        executeScripts();
        initUsers();
        initRoles();
    }

    private void executeScripts() {
        List<Role> roles = roleRepository.findAll();
        if(roles.isEmpty()) {
            executeScript("user.sql");
            executeScript("role.sql");
        }
    }

    @Transactional
    private void initRoles() {
        List<Role> roles = roleRepository.findAll();
        List<User> users = userRepository.findAll();

        Optional<User> admin = users.stream().filter(e -> e.getUsername().equals("admin")).findFirst();
        Optional<User> user = users.stream().filter(e -> e.getUsername().equals("user")).findFirst();
        Role adminRole = Role.builder().rolename("ADMIN").user(admin.get()).build();
        Role userRole = Role.builder().rolename("USER").user(user.get()).build();

        if(roles.isEmpty()) {
            roleRepository.save(adminRole);
            roleRepository.save(userRole);
        }

    }

    @Transactional
    private void initUsers() {
        List<User> users = userRepository.findAll();
        List<Role> roles = roleRepository.findAll();

        Optional<Role> adminRole = roles.stream().filter(e -> e.getRolename().equals("ADMIN")).findAny();
        Optional<Role> userRole = roles.stream().filter(e -> e.getRolename().equals("USER")).findAny();




        if(users.isEmpty()) {

            String password = "123";

            // Generate Salt. The generated value can be stored in DB.
            String salt = PasswordUtils.getSalt(30);

            // Protect user's password. The generated value can be stored in DB.
            password = PasswordUtils.generateSecurePassword(password);

            // Print out protected password
            System.out.println("My secure password = " + password);
            System.out.println("Salt value = " + salt);

            User admin = User.builder()
                    .username("admin")
                    .password(password)
                    .salt(salt)
                    //.roles(Arrays.asList(adminRole.get(), userRole.get()))
                    .build();

            userRepository.save(admin);

            password = "456";

            // Generate Salt. The generated value can be stored in DB.
            salt = PasswordUtils.getSalt(30);

            // Protect user's password. The generated value can be stored in DB.
            password = PasswordUtils.generateSecurePassword(password);

            // Print out protected password
            System.out.println("My secure password = " + password);
            System.out.println("Salt value = " + salt);

            User user = User.builder()
                    .username("user")
                    .password(password)
                    .salt(salt)
                    //.roles(Arrays.asList(userRole.get()))
                    .build();

            userRepository.save(user);

        }

    }

    public void executeScript(String script) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(SCRIPTS_PATH + script)));
        String line = "";

        String sql = "";

        try {
            while ((line = reader.readLine()) != null) {
                if(line.trim().isEmpty()) {
                    continue;
                }
                sql = sql + line;
            }
            reader.close();
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }


        try (Connection connect = dataSource.getConnection();
             Statement statement = connect.createStatement()) {

            statement.executeUpdate(sql);

            log.info("Script " + script + " ejecutado exitosamente");

        } catch (SQLException e) {
            String errorMsg = "Error al ejecutar el script " + script;
            log.error(e.getMessage());
            //throw new Exception(e.getMessage());
        }

    }
}