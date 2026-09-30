package animal.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import animal.common.Animal;

/**
 * Le LANCEUR du serveur : il cree le servant et le publie.
 *
 * Deux modes, comme en TD 1 :
 *   java animal.server.Server             -> registre externe (rmiregistry)
 *   java animal.server.Server --embedded  -> registre cree dans cette JVM
 */

public class Server {

    public static final int PORT = 1099;

    public static void main(String[] args) {
        boolean embedded = args.length > 0 && "--embedded".equals(args[0]);
        try {
            Animal obj = new AnimalImpl("Goldy", "Heythem", "Golden Retreiver", "Dog");
            Registry registry = embedded
                    ? LocateRegistry.createRegistry(PORT)
                    : LocateRegistry.getRegistry(PORT);
            registry.rebind("Animal", obj);
            System.out.println("Server ready(registre "
                        + (embedded ? "interne" : "externe") + ", port " + PORT + ")");
        } catch (Exception e) {
            System.err.println("Server exception: " + e);
            e.printStackTrace();
        }
    }
}