package cl.dsoto.jwt.controller;

import com.sun.enterprise.config.serverbeans.customvalidators.JDBCRealmPropertyCheck;
import fish.payara.security.annotations.RealmIdentityStoreDefinition;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Created by root on 09-12-22.
 */
@ApplicationPath("rest")
public class JAXRSConfiguration extends Application {

}
