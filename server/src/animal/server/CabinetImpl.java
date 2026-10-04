package animal.server;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import animal.common.*;

public class CabinetImpl extends UnicastRemoteObject implements Cabinet {
    private final String nom;
    private final List<Animal> patients = new CopyOnWriteArrayList<>();

    private static final int[] SEUILS = {100, 500, 1000};
    private final List<AlerteObserver> observers = new CopyOnWriteArrayList<>();

    @Override
    public void abonner(AlerteObserver observer) throws RemoteException {
        if (!observers.contains(observer)) observers.add(observer);
    }

    @Override
    public void desabonner(AlerteObserver observer) throws RemoteException {
        observers.remove(observer);
    }

    private void notifierFranchissement(int avant, int apres) throws RemoteException {
        for (int seuil : SEUILS) {
            boolean hausse = avant < seuil && apres >= seuil;
            boolean baisse = avant >= seuil && apres <  seuil;
            if (hausse || baisse) {
                diffuser(seuil, apres, hausse);
            }
        }
    }

    private void diffuser(int seuil, int nombre, boolean hausse) throws RemoteException {
        for (AlerteObserver observer : observers) {
            try {
                observer.notifier(seuil, nombre, hausse);
            } catch (RemoteException exception) {
                observers.remove(observer);
            }
        }
    }

    @Override
    public void retirerPatient(String nom) throws RemoteException, PatientIntrouvableException {
        int avant, apres;
        synchronized (this) {
            Animal cible = null;
            for (Animal a : patients) {
                if (a.getNom().equalsIgnoreCase(nom)) { cible = a; break; }
            }
            if (cible == null) throw new PatientIntrouvableException(nom);
            avant = patients.size();
            patients.remove(cible);
            apres = patients.size();
        }
        notifierFranchissement(avant, apres);
    }

    public CabinetImpl(String nom) throws RemoteException {
        super();
        this.nom = nom;
    }
    // pour ajouter un animal dans le serveur directement
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
    @Override
    public synchronized Animal ajouterPatient(String nom, String maitre, String race, Espece espece, String texteDossier) throws RemoteException, PatientDejaExistantException {
        int avant, apres;
        for (Animal a : patients) {
            if (a.getNom().equalsIgnoreCase(nom)) {
                throw new  PatientDejaExistantException(nom);
            }
        }

        avant = patients.size();
        Animal a = new AnimalImpl(nom, maitre, race, espece, texteDossier);
        patients.add(a);
        apres = patients.size();
        notifierFranchissement(avant, apres);
        return a;
    }
    @Override
    public int getNombrePatients() throws RemoteException {
        return patients.size();
    }
}
