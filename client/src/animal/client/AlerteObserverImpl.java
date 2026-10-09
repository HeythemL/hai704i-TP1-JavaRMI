package animal.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.function.Consumer;

import animal.common.AlerteObserver;

public class AlerteObserverImpl extends UnicastRemoteObject implements AlerteObserver {

    private Consumer<String> afficheur;

    public AlerteObserverImpl(Consumer<String> afficheur) throws RemoteException {
        super();
        this.afficheur = afficheur;
    }

    @Override
    public void notifier(int seuil, int nombre, boolean hausse) {
        String direction = hausse ? "franchi a la hausse" : "franchi a la baisse";
        String message = "Seuil " + seuil + " " + direction + " : " + nombre + " patients";
        afficheur.accept(message);
    }
}
