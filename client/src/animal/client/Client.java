package animal.client;

import java.lang.reflect.Proxy;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import animal.common.Animal;

/**
 * Le CLIENT. Il ne connait que l'interface Animal, jamais AnimalImpl.
 *
 * Usage : java animal.client.Client [hote]
 *         (l'argument est un NOM D'HOTE, pas un numero de port)
 */

public class Client {

    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host, 1099);
            Animal stub = (Animal) registry.lookup("Animal");
            System.out.println("classe du stub : " + stub.getClass().getName());
            System.out.println("proxy dynamique ? " + Proxy.isProxyClass(stub.getClass()));

            System.out.println("response: " + stub.getNom() + " " + stub.getMaitre());
        } catch (Exception e) {
            System.err.println("Client exception: " + e);
            e.printStackTrace();
        }
    }
    
}
