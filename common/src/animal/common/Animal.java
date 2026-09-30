package animal.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * L'interface distante : le CONTRAT partage.
 *
 * C'est le seul type que le client et le serveur doivent connaitre tous
 * les deux. Il vit donc dans le projet common, ajoute au classpath des
 * deux autres projets.
 */

public interface Animal extends Remote {
    String getNom() throws RemoteException;
    String getMaitre() throws RemoteException;
    String getRace() throws RemoteException;
    Espece getEspece() throws RemoteException;  //A2 : String -> Espece
    Dossier getDossier() throws RemoteException;
}