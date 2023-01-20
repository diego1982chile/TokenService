package cl.forevision.jwt.repositories;


import cl.forevision.jwt.entities.Role;
import cl.forevision.jwt.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by root on 13-10-22.
 */
public interface RoleRepository extends JpaRepository<Role, String> {

    @Query("SELECT r FROM Role r where r.rolename = :rolename")
    Role findByRolename(@Param("rolename") String rolename);

    @Query("SELECT r FROM Role r order by r.rolename")
    List<Role> findAllOrderByName();

    @Modifying
    @Query("delete from Role r")
    void removeAll();

}
