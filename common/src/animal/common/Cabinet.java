package animal.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Cabinet extends Remote {
    String getNom() throws RemoteException;
    List<Animal> getPatients() throws RemoteException;
    Animal rechercherParNom(String nom) throws RemoteException, PatientIntrouvableException;
}
