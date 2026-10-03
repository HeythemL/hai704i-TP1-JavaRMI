package animal.server;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import animal.common.Animal;
import animal.common.Cabinet;
import animal.common.PatientIntrouvableException;

public class CabinetImpl extends UnicastRemoteObject implements Cabinet {
    private final String nom;
    private final List<Animal> patients = new CopyOnWriteArrayList<>();

    public CabinetImpl(String nom) throws RemoteException {
        super();
        this.nom = nom;
    }

    public void ajouterLocalement(Animal a) throws RemoteException {
        patients.add(a);
    }
    @Override
    public String getNom() throws RemoteException {
        return nom;
    }
    @Override
    public List<Animal> getPatients() throws RemoteException {
        return new ArrayList<>(patients);
    }
    @Override
    public Animal rechercherParNom(String nom) throws RemoteException, PatientIntrouvableException {
        for (Animal a : patients) {
            if (a.getNom().equals(nom)) {
                return a;
            }
        }
        throw new PatientIntrouvableException(nom);
    }
}
