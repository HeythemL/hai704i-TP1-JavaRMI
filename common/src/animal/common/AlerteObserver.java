package animal.common;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AlerteObserver extends Remote{
    void notifier(int seuil, int nombre, boolean hausse) throws RemoteException;
}
