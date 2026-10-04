package animal.client;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.function.Consumer;
import animal.common.AlerteObserver;

/** Servant eberge par le CLIENT. Le texte de l'alerte est confie a un afficheur. */
public class AlerteObserverImpl extends UnicastRemoteObject implements AlerteObserver {
    private final Consumer<String> afficheur;
    public AlerteObserverImpl(Consumer<String> afficheur) throws RemoteException {
        super();   // export
        this.afficheur = afficheur;
    }
    @Override
    public void notifier(int seuil, int nombre, boolean hausse) {
        afficheur.accept("Seuil " + seuil + (hausse ? " franchi a la hausse" : " franchi a la baisse")
                + " : " + nombre + " patients");
    }
}
