package io.belov.soyuz.ssh;

import com.google.common.base.Throwables;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.KeyPair;

import java.io.ByteArrayOutputStream;

/**
 * Created by fbelov on 03.02.16.
 */
public class SshKeyGenerator {

    private JSch jsch = new JSch();

    public synchronized SshKeyPair generate() {
        try {
            KeyPair kpair= KeyPair.genKeyPair(jsch, KeyPair.RSA);
            ByteArrayOutputStream privateStream = new ByteArrayOutputStream();
            ByteArrayOutputStream publicStream = new ByteArrayOutputStream();

            kpair.writePrivateKey(privateStream);
            kpair.writePublicKey(publicStream, "");

            return new SshKeyPair(privateStream.toString("UTF-8"), publicStream.toString("UTF-8"));
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

}
