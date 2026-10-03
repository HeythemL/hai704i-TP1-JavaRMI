package animal.client;

import java.lang.reflect.Proxy;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;

import animal.common.Animal;
import animal.common.Cabinet;
import animal.common.Dossier;
import animal.common.Espece;
import animal.common.PatientIntrouvableException;

/**
 * Le CLIENT. Il ne connait que l'interface Animal et la classe Espece, jamais AnimalImpl.
 *
 * Usage : java animal.client.Client [hote]
 *         (l'argument est un NOM D'HOTE, pas un numero de port)
 */

public class Client {

    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host, 1099);
            // Animal stub

            System.out.println(Arrays.toString(registry.list()));
            Cabinet cabinet = (Cabinet) registry.lookup("Cabinet");
            List<Animal> patients = cabinet.getPatients();
            System.out.println("Nombre de patients : " + patients.size());
            for (Animal a : patients) {
                System.out.println(" - " + a.getNom() + " | " + a.getClass().getName()
                        + " | proxy ? " + Proxy.isProxyClass(a.getClass()));
            }
            Animal stub = cabinet.rechercherParNom("Goldy");
            System.out.println("Trouve : " + stub.getNom() + ", maitre " + stub.getMaitre());
            try {
                cabinet.rechercherParNom("Felix");
            } catch (PatientIntrouvableException ex) {
                System.out.println("Recherche infructueuse : " + ex.getMessage());
            }            System.out.println("classe du stub : " + stub.getClass().getName());
            System.out.println("proxy dynamique ? " + Proxy.isProxyClass(stub.getClass()));
            System.out.println("response: " + stub.getNom() + " " + stub.getMaitre());
            // Espece
            Espece e1 = stub.getEspece();
            System.out.println("Espece 1 recue : " + e1);
            System.err.println("idhashcode(e1) : " + System.identityHashCode(e1));
            //Modification de la copie locale (cote client)
            e1.setNom("Cat");
            System.out.println("Espece 1 apres la modification locale : " + e1.getNom());

            //recuperation de l'espece depuis le serveur
            Espece e2 = stub.getEspece();
            System.err.println("Espece 2 obtenue du serveur : " + e2.getNom());
            System.err.println("idhashcode(e2) : " + System.identityHashCode(e2));
            //nrmlm output original
            //
            Dossier d1 = stub.getDossier();
            System.out.println("Dossier initial : " + d1.getTexte());
            //modification distante par le client :
            d1.setTexte("Consultation du 05/10 : Traitement prescrit");
            System.out.println("Modification envoyee au serveur");
            //relecture pour verifier si la modification a ete effectee
            Dossier d2 = stub.getDossier();
            System.out.println("Dossier initial : " + d2.getTexte());

        } catch (Exception e) {
            System.err.println("Client exception: " + e);
            e.printStackTrace();
        }
    }
}
