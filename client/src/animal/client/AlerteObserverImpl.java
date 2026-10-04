package animal.client;

import animal.common.AlerteObserver;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AlerteObserverImpl extends UnicastRemoteObject implements AlerteObserver {
    public AlerteObserverImpl() throws RemoteException {
        super();
    }
    @Override
    public void notifier(int seuil, int nombre, boolean hausse) throws RemoteException {
        System.out.println("Alerte : seuile : " + seuil + " hausse : " + (hausse ? "franchi a la hausse" : "franchi a la baisse" + " : " + nombre + "patients") );
    }
}
