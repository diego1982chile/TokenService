package cl.dsoto.jwt.repositories;


import cl.dsoto.jwt.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by root on 13-10-22.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {


}
