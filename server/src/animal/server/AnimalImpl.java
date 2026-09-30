package animal.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import animal.common.Animal;

public class AnimalImpl extends UnicastRemoteObject implements Animal {
    private final String nom;
    private final String maitre;
    private final String race;
    private final String espece;
    
    public AnimalImpl(String nom, String maitre, String race, String espece) throws RemoteException {
        super();
        this.nom = nom;
        this.maitre = maitre;
        this.race = race;
        this.espece = espece;

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
    public String getEspece() throws RemoteException {
        return espece;
    }
    
}