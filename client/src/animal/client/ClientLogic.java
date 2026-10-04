package animal.client;

import java.rmi.ConnectException;
import java.rmi.ConnectIOException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import animal.common.Animal;
import animal.common.Cabinet;
import animal.common.Espece;
import animal.common.PatientDejaExistantException;
import animal.common.PatientIntrouvableException;

/** Logique client : seule couche qui manipule les interfaces distantes. */
public class ClientLogic {

    private final Cabinet cabinet;
    private AlerteObserverImpl observateur;   // non null <=> abonne

    public ClientLogic(Cabinet cabinet) { this.cabinet = cabinet; }

    public List<PatientVue> listerPatients() throws ClientException {
        try {
            List<PatientVue> vues = new ArrayList<>();
            for (Animal a : cabinet.getPatients()) vues.add(vue(a));
            return vues;
        } catch (RemoteException e) { throw traduire(e); }
    }

    public PatientVue rechercher(String nom) throws ClientException {
        try { return vue(cabinet.rechercherParNom(nom)); }
        catch (PatientIntrouvableException e) { throw new ClientException(e.getMessage()); }
        catch (RemoteException e) { throw traduire(e); }
    }

    public PatientVue enregistrer(String nom, String maitre, String race,
                                  String espece, int esperanceVie, String texteDossier) throws ClientException {
        try { return vue(cabinet.ajouterPatient(nom, maitre, race, new Espece(espece, esperanceVie), texteDossier)); }
        catch (PatientDejaExistantException e) { throw new ClientException(e.getMessage()); }
        catch (RemoteException e) { throw traduire(e); }
    }

    public int nombrePatients() throws ClientException {
        try { return cabinet.getNombrePatients(); }
        catch (RemoteException e) { throw traduire(e); }
    }

    public String lireDossier(String nomPatient) throws ClientException {
        try { return cabinet.rechercherParNom(nomPatient).getDossier().getTexte(); }
        catch (PatientIntrouvableException e) { throw new ClientException(e.getMessage()); }
        catch (RemoteException e) { throw traduire(e); }
    }

    public void modifierDossier(String nomPatient, String texte) throws ClientException {
        try { cabinet.rechercherParNom(nomPatient).getDossier().setTexte(texte); }
        catch (PatientIntrouvableException e) { throw new ClientException(e.getMessage()); }
        catch (RemoteException e) { throw traduire(e); }
    }

    // ---- alertes : le client HEBERGE un objet distant ----

    public boolean estAbonne() { return observateur != null; }

    public void sAbonner(Consumer<String> afficheur) throws ClientException {
        if (observateur != null) throw new ClientException("Vous etes deja abonne.");
        AlerteObserverImpl obs = null;
        try {
            obs = new AlerteObserverImpl(afficheur);   // export
            cabinet.abonner(obs);                      // le serveur recoit le STUB
            observateur = obs;
        } catch (RemoteException e) {
            desexporter(obs);
            throw traduire(e);
        }
    }

    public void seDesabonner() throws ClientException {
        if (observateur == null) throw new ClientException("Vous n'etes pas abonne.");
        try { cabinet.desabonner(observateur); }
        catch (RemoteException e) { throw traduire(e); }
        finally {
            desexporter(observateur);   // sinon l'objet exporte garde la JVM en vie
            observateur = null;
        }
    }

    /** A appeler avant de quitter. */
    public void fermer() {
        if (observateur != null) {
            try { seDesabonner(); } catch (ClientException ignoree) { /* on quitte de toute facon */ }
        }
    }

    // ---- utilitaires ----

    private static void desexporter(AlerteObserverImpl obs) {
        if (obs == null) return;
        try { UnicastRemoteObject.unexportObject(obs, true); }
        catch (NoSuchObjectException ignoree) { }
    }

    private static PatientVue vue(Animal a) throws RemoteException {
        return new PatientVue(a.getNom(), a.getMaitre(), a.getRace(), a.getEspece().getNom());
    }

    /** RemoteException -> message pour l'utilisateur (on lit les causes de l'interieur). */
    private static ClientException traduire(RemoteException e) {
        Throwable racine = e;
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof ConnectException || t instanceof ConnectIOException) {
                return new ClientException("Impossible de joindre le serveur (est-il arrete ?).");
            }
            if (t instanceof NoSuchObjectException) {
                return new ClientException("L'objet distant n'existe plus cote serveur (serveur redemarre ?).");
            }
            racine = t;
        }
        return new ClientException("Erreur de communication avec le serveur : " + racine.getMessage());
    }
}
