package animal.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import animal.common.Cabinet;

public class ClientMain {

    public static void main(String[] args) {

        String hote = null;
        int port = 1099;

        if (args.length >= 1) {
            hote = args[0];
        }

        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Port invalide : " + args[1]);
                System.exit(2);
            }
        }

        Cabinet cabinet;

        try {
            Registry registry = LocateRegistry.getRegistry(hote, port);
            cabinet = (Cabinet) registry.lookup("Cabinet");
        } catch (NotBoundException e) {
            System.err.println("Le service \"Cabinet\" n'est pas publie : le serveur est-il lance ?");
            System.exit(1);
            return;
        } catch (RemoteException e) {
            String adresse = (hote == null) ? "localhost" : hote;
            System.err.println("Impossible de joindre le registre sur " + adresse + ":" + port);
            System.exit(1);
            return;
        }

        ClientLogic logique = new ClientLogic(cabinet);
        ConsoleUI ui = new ConsoleUI(logique);
        ui.lancer();
    }
}
