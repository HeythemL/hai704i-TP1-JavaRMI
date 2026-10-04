package animal.common;

public class PatientDejaExistantException extends Exception {
    private static final long serialVersionUID = 1L;
    public PatientDejaExistantException(String nom) {
        super("Un patient nomme " + nom + " existe deja");
    }
}
