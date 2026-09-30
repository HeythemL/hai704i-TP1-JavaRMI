package animal.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import animal.common.Animal;
import animal.common.Dossier;
import animal.common.Espece;

public class AnimalImpl extends UnicastRemoteObject implements Animal {
    private final String nom;
    private final String maitre;
    private final String race;
    private final Espece espece;
    private final Dossier dossier;
    
    public AnimalImpl(String nom, String maitre, String race, Espece espece, String texteDossier) throws RemoteException {
        super();
        this.nom = nom;
        this.maitre = maitre;
        this.race = race;
        this.espece = espece;
        this.dossier = new DossierImpl(texteDossier);
    }

    @Override
    public String getNom() throws RemoteException {
        return nom;
    }
    @Override
    public String getMaitre() throws RemoteException {
        return maitre;
    }
    @Override
    public String getRace() throws RemoteException {
        return race;
    }
    @Override
    public Espece getEspece() throws RemoteException {
        return espece;
    }
    @Override
    public Dossier getDossier() throws RemoteException {
        return dossier;
    }
    
}