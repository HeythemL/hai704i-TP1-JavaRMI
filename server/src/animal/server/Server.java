package animal.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import animal.common.Animal;
import animal.common.Espece;

public class Server {
    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");

            Registry registry = LocateRegistry.createRegistry(1099);
            System.out.println("RMI Registry started on port 1099.");

            Espece espece = new Espece("Dog", 13);
            AnimalImpl animal = new AnimalImpl("Goldy", "Alice", "Retriever", espece, "Vaccins a jour.");

            System.out.println("Server is ready and 'Cabinet' is published.");
            CabinetImpl cabinet = new CabinetImpl("Clinique du Triolet");
            cabinet.ajouterLocalement(new AnimalImpl("Felix", "Alice", "Siamois", new Espece("Chat", 15), "Vaccins a jour"));
            cabinet.ajouterLocalement(new AnimalImpl("Jack" , "Heythem", "Berger", espece,  "Vaccins a jour"));
            cabinet.ajouterLocalement(new AnimalImpl("Goldy" , "Heythem", "Berger", espece,  "Vaccins a jour"));
            registry.rebind("Cabinet", cabinet);

        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}