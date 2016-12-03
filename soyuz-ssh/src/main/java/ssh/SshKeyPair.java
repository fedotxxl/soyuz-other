package ssh;

/**
 * Created by fbelov on 03.02.16.
 */
public class SshKeyPair {

    private String keyPrivate;
    private String keyPublic;

    public SshKeyPair() {
    }

    public SshKeyPair(String keyPrivate, String keyPublic) {
        this.keyPrivate = keyPrivate;
        this.keyPublic = keyPublic;
    }

    public String getKeyPrivate() {
        return keyPrivate;
    }

    public String getKeyPublic() {
        return keyPublic;
    }

}
