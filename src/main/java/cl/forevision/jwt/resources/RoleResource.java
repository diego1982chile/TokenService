package cl.forevision.jwt.resources;


import cl.forevision.jwt.entities.Role;
import cl.forevision.jwt.entities.User;
import cl.forevision.jwt.services.RoleService;
import cl.forevision.jwt.services.UserService;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.security.Principal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by des01c7 on 12-12-19.
 */
@RequestScoped
@Produces(APPLICATION_JSON)
@Path("roles")
@RolesAllowed({"ADMIN"})
public class RoleResource {

    @Inject
    RoleService roleService;

    @Inject
    Principal principal;

    static private final Logger logger = Logger.getLogger(RoleResource.class.getName());

    @GET
    public Response getAllRoles() {
        try {
            List<Role> roles = roleService.getAllRoles();
            return Response.ok(roles).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }

    @POST
    @Path("save")
    public Response createRole(Role role) {
        try {
            Role newRole = roleService.saveRole(role);
            return Response.ok(newRole).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }

    @PUT
    @Path("update")
    public Response updateRole(Role role) {
        try {
            Role newRole = roleService.updateRole(role);
            return Response.ok(newRole).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }

    @DELETE
    @Path("delete/{id}")
    public Response deleteUser(@PathParam("id") String id) {
        try {
            roleService.deleteRole(id);
            return Response.ok().build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }

}
