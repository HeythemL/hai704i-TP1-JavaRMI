package animal.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import animal.common.Cabinet;

/** Point d'entree : java animal.client.ClientMain [hote] [port] */
public class ClientMain {

    public static void main(String[] args) {
        String hote = (args.length < 1) ? null : args[0];   // null = machine locale
        int port = 1099;
        if (args.length >= 2) {
            try { port = Integer.parseInt(args[1]); }
            catch (NumberFormatException e) {
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
            System.err.println("Impossible de joindre le registre sur "
                    + (hote == null ? "localhost" : hote) + ":" + port + " (registre ou serveur absent ?).");
            System.exit(1);
            return;
        }

        new ConsoleUI(new ClientLogic(cabinet)).lancer();
        // pas de System.exit ici : si le processus ne se termine pas, c'est un objet encore exporte
    }
}
