package animal.client;
/** Erreur destinee a l'utilisateur : le message est deja comprehensible. */
public class ClientException extends Exception {
    public ClientException(String message) { super(message); }
}
