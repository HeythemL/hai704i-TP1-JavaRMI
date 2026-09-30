package animal.common;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Dossier extends Remote {
    String getTexte() throws RemoteException;
    void setTexte(String texte) throws RemoteException;
}
