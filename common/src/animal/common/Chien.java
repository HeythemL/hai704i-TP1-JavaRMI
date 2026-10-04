package animal.common;

import java.io.Serial;

public class Chien extends Espece {

    @Serial
    private static final long serialVersionUID = 1L;

    public Chien() {
        super("Chien" , 13);
    }
}
