package animal.common;

import java.io.Serial;

public class PatientIntrouvableException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public PatientIntrouvableException(String nom) {
        super("Aucun patient aven le nom : " + nom);
    }
}
