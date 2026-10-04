package animal.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import animal.common.PatientDejaExistantException;
public interface Cabinet extends Remote {
    String getNom() throws RemoteException;
    List<Animal> getPatients() throws RemoteException;
    Animal rechercherParNom(String nom) throws RemoteException, PatientIntrouvableException;
    Animal ajouterPatient(String nom, String maitre, String race, Espece espece, String texteDossier) throws RemoteException, PatientDejaExistantException;
    int getNombrePatients() throws RemoteException;
    // A6
    void abonner(AlerteObserver observer) throws RemoteException;
    void desabonner(AlerteObserver observer) throws RemoteException;
    void retirerPatient(String nom) throws RemoteException, PatientIntrouvableException;
}
