package animal.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import animal.common.Dossier;

public class DossierImpl extends UnicastRemoteObject implements Dossier {
    private String texte;

    public DossierImpl(String texte) throws RemoteException {
        super();
        this.texte = texte;
    }

    @Override
    public synchronized String getTexte() throws RemoteException{ //syncrhronized guarantees only one thread at a time can execute any of the methodes on the instance of DossierImpl
        return texte;
    }
    @Override 
    public synchronized void setTexte(String texte) throws RemoteException {
        this.texte = texte;
    }
}