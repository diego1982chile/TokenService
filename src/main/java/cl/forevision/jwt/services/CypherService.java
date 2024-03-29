package cl.forevision.jwt.services;

import cl.forevision.jwt.resources.RoleResource;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import lombok.extern.log4j.Log4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by root on 09-12-22.
 */
@Log4j
public class CypherService {


    public static String generateJWT(PrivateKey key, String subject, List<String> groups) {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT)
                .keyID("apisKey")
                .build();

        MPJWTToken token = new MPJWTToken();
        token.setAud("apisGt");
        //token.setIss("https://apis.internal.forevision.cl");  // Must match the expected issues configuration values
        token.setIss("https://apis.internal.dsoto.cl");  // Must match the expected issues configuration values
        token.setJti(UUID.randomUUID().toString());

        token.setSub(subject);
        token.setUpn(subject);

        token.setIat(System.currentTimeMillis());
        //token.setExp(System.currentTimeMillis() + 7*24*60*60*1000); // 1 week expiration!
        token.setExp(System.currentTimeMillis() + 60*60*1000); // 1 hour expiration!

        token.setGroups(groups);

        JWSObject jwsObject = new JWSObject(header, new Payload(token.toJSONString()));

        // Apply the Signing protection
        JWSSigner signer = new RSASSASigner(key);

        try {
            jwsObject.sign(signer);
        } catch (JOSEException e) {
            log.error(e.getMessage());
        }

        return jwsObject.serialize();
    }

    public PrivateKey readPrivateKey() throws IOException {

        InputStream inputStream = CypherService.class.getResourceAsStream("/privateKey.pem");

        PEMParser pemParser = new PEMParser(new InputStreamReader(inputStream));
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(new BouncyCastleProvider());
        Object object = pemParser.readObject();
        KeyPair kp = converter.getKeyPair((PEMKeyPair) object);
        return kp.getPrivate();
    }

}
