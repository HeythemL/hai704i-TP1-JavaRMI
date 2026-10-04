package animal.client;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import animal.common.Cabinet;
import animal.common.Espece;


public class TestAlertes {
    public static void main(String[] args) throws Exception {
        Cabinet cabinet = (Cabinet) LocateRegistry.getRegistry(null,1099).lookup("Cabinet");

        switch (args[0]) {
            case "ecoute" -> {
                cabinet.abonner(new AlerteObserverImpl());
                System.out.println("Abonne");
                Thread.sleep(Long.MAX_VALUE);
            }
            case "remplir" -> {
                int i = 0;
                while (cabinet.getNombrePatients() < 100) {
                    cabinet.ajouterPatient("P" + (i++), "m", "r", new Espece("Chien" , 10), "");
                }
                System.out.println("Nombre de patients : " + cabinet.getNombrePatients());
            }
            case "ajouter" -> {
                cabinet.ajouterPatient(args[1], "m", "r", new Espece("Chien", 10), "");
                System.out.println("Nombre de patients : " + cabinet.getNombrePatients());
            }
            case "retirer" -> {
                cabinet.retirerPatient(args[1]);
                System.out.println("Nombre de patients : " + cabinet.getNombrePatients());
            }
            default -> System.out.println("Mode inconnu");
        }
    }
}
