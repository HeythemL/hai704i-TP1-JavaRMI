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

public class ClientLogic {

    private Cabinet cabinet;
    private AlerteObserverImpl observateur;

    public ClientLogic(Cabinet cabinet) {
        this.cabinet = cabinet;
    }

    public List<PatientVue> listerPatients() throws ClientException {
        try {
            List<PatientVue> vues = new ArrayList<>();
            for (Animal a : cabinet.getPatients()) {
                vues.add(toVue(a));
            }
            return vues;
        } catch (RemoteException e) {
            throw traduire(e);
        }
    }

    public PatientVue rechercher(String nom) throws ClientException {
        try {
            return toVue(cabinet.rechercherParNom(nom));
        } catch (PatientIntrouvableException e) {
            throw new ClientException(e.getMessage());
        } catch (RemoteException e) {
            throw traduire(e);
        }
    }

    public PatientVue enregistrer(String nom, String maitre, String race,
                                  String especeNom, int esperanceVie, String texteDossier) throws ClientException {
        try {
            Espece espece = new Espece(especeNom, esperanceVie);
            Animal animal = cabinet.ajouterPatient(nom, maitre, race, espece, texteDossier);
            return toVue(animal);
        } catch (PatientDejaExistantException e) {
            throw new ClientException(e.getMessage());
        } catch (RemoteException e) {
            throw traduire(e);
        }
    }

    public int nombrePatients() throws ClientException {
        try {
            return cabinet.getNombrePatients();
        } catch (RemoteException e) {
            throw traduire(e);
        }
    }

    public String lireDossier(String nomPatient) throws ClientException {
        try {
            Animal animal = cabinet.rechercherParNom(nomPatient);
            return animal.getDossier().getTexte();
        } catch (PatientIntrouvableException e) {
            throw new ClientException(e.getMessage());
        } catch (RemoteException e) {
            throw traduire(e);
        }
    }

    public void modifierDossier(String nomPatient, String texte) throws ClientException {
        try {
            Animal animal = cabinet.rechercherParNom(nomPatient);
            animal.getDossier().setTexte(texte);
        } catch (PatientIntrouvableException e) {
            throw new ClientException(e.getMessage());
        } catch (RemoteException e) {
            throw traduire(e);
        }
    }

    public boolean estAbonne() {
        return observateur != null;
    }

    public void sAbonner(Consumer<String> afficheur) throws ClientException {
        if (observateur != null) {
            throw new ClientException("Vous etes deja abonne.");
        }
        AlerteObserverImpl obs = null;
        try {
            obs = new AlerteObserverImpl(afficheur);
            cabinet.abonner(obs);
            observateur = obs;
        } catch (RemoteException e) {
            desexporter(obs);
            throw traduire(e);
        }
    }

    public void seDesabonner() throws ClientException {
        if (observateur == null) {
            throw new ClientException("Vous n'etes pas abonne.");
        }
        try {
            cabinet.desabonner(observateur);
        } catch (RemoteException e) {
            throw traduire(e);
        } finally {
            desexporter(observateur);
            observateur = null;
        }
    }

    public void fermer() {
        if (observateur != null) {
            try {
                seDesabonner();
            } catch (ClientException e) {
                // on quitte de toute facon
            }
        }
    }

    private static void desexporter(AlerteObserverImpl obs) {
        if (obs == null) {
            return;
        }
        try {
            UnicastRemoteObject.unexportObject(obs, true);
        } catch (NoSuchObjectException e) {
            // deja desexporte
        }
    }

    private static PatientVue toVue(Animal a) throws RemoteException {
        return new PatientVue(a.getNom(), a.getMaitre(), a.getRace(), a.getEspece().getNom());
    }

    private static ClientException traduire(RemoteException e) {
        Throwable cause = e;
        while (cause != null) {
            if (cause instanceof ConnectException || cause instanceof ConnectIOException) {
                return new ClientException("Impossible de joindre le serveur (est-il arrete ?).");
            }
            if (cause instanceof NoSuchObjectException) {
                return new ClientException("L'objet distant n'existe plus cote serveur (serveur redemarre ?).");
            }
            cause = cause.getCause();
        }
        return new ClientException("Erreur de communication avec le serveur : " + e.getMessage());
    }
}
