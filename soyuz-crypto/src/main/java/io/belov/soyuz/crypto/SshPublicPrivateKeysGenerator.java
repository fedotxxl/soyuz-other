package io.belov.soyuz.crypto;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * Created by fbelov on 22.11.15.
 */
public class SshPublicPrivateKeysGenerator {

    private static final Logger log = LoggerFactory.getLogger(SshPublicPrivateKeysGenerator.class);

    //https://gist.github.com/liudong/3993726
    public Keys generate(String keyAlgorithm, int numBits) {
        try {
            // Get the public/private key pair
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(keyAlgorithm);
            keyGen.initialize(numBits);
            KeyPair keyPair = keyGen.genKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            log.debug("Generating key/value pair using {} algorithm", privateKey.getAlgorithm());

            // Get the bytes of the public and private keys
            byte[] privateKeyBytes = privateKey.getEncoded();
            byte[] publicKeyBytes = publicKey.getEncoded();

            String keyPublic = Base64.getEncoder().encodeToString(publicKeyBytes);
            String keyPrivate = Base64.getEncoder().encodeToString(privateKeyBytes);

            return new Keys(keyPublic, keyPrivate);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }

    }

    public static class Keys {
        private String keyPublic;
        private String keyPrivate;

        public Keys(String keyPublic, String keyPrivate) {
            this.keyPublic = keyPublic;
            this.keyPrivate = keyPrivate;
        }

        public String getKeyPublic() {
            return keyPublic;
        }

        public String getKeyPrivate() {
            return keyPrivate;
        }

        @Override
        public String toString() {
            return "Keys{" +
                    "public='" + keyPublic + '\'' +
                    ", private='" + keyPrivate + '\'' +
                    '}';
        }
    }

}
