package animal.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ConsoleUI {

    private ClientLogic logique;
    private BufferedReader in;
    private volatile String promptCourant;

    public ConsoleUI(ClientLogic logique) {
        this.logique = logique;
        this.in = new BufferedReader(new InputStreamReader(System.in));
        this.promptCourant = "";
    }

    public void lancer() {
        afficher("=== Cabinet veterinaire ===");
        try {
            boolean continuer = true;
            while (continuer) {
                afficherMenu();
                String choix = lire("Votre choix > ").trim();
                continuer = traiter(choix);
            }
        } catch (FinEntree e) {
            afficher("Fin de l'entree : fermeture du client.");
        } finally {
            logique.fermer();
        }
        afficher("Au revoir.");
    }

    private void afficherMenu() {
        String statut = logique.estAbonne() ? "abonne" : "non abonne";
        afficher("");
        afficher("--- Menu (alertes : " + statut + ") ---");
        afficher("1. Lister les patients");
        afficher("2. Rechercher un patient par nom");
        afficher("3. Enregistrer un nouveau patient");
        afficher("4. Consulter le dossier d'un patient");
        afficher("5. Mettre a jour le dossier d'un patient");
        afficher("6. S'abonner aux alertes");
        afficher("7. Se desabonner des alertes");
        afficher("0. Quitter");
    }

    private boolean traiter(String choix) {
        try {
            if (choix.equals("1")) {
                lister();
            } else if (choix.equals("2")) {
                String nom = lireNonVide("Nom du patient > ");
                PatientVue p = logique.rechercher(nom);
                afficher(decrire(p));
            } else if (choix.equals("3")) {
                enregistrer();
            } else if (choix.equals("4")) {
                String nom = lireNonVide("Nom du patient > ");
                String dossier = logique.lireDossier(nom);
                afficher("Dossier de " + nom + " : " + dossier);
            } else if (choix.equals("5")) {
                String nom = lireNonVide("Nom du patient > ");
                String texte = lireNonVide("Nouveau texte du dossier > ");
                logique.modifierDossier(nom, texte);
                afficher("Dossier mis a jour.");
            } else if (choix.equals("6")) {
                logique.sAbonner(this::afficherAlerte);
                afficher("Abonne aux alertes.");
            } else if (choix.equals("7")) {
                logique.seDesabonner();
                afficher("Desabonne des alertes.");
            } else if (choix.equals("0")) {
                return false;
            } else {
                afficher("Choix inconnu : \"" + choix + "\". Tapez un chiffre du menu.");
            }
        } catch (ClientException e) {
            afficher("Erreur : " + e.getMessage());
        }
        return true;
    }

    private void lister() throws ClientException {
        List<PatientVue> patients = logique.listerPatients();
        if (patients.isEmpty()) {
            afficher("Aucun patient.");
            return;
        }
        afficher(patients.size() + " patient(s) :");
        for (PatientVue p : patients) {
            afficher(" - " + decrire(p));
        }
    }

    private void enregistrer() throws ClientException {
        String nom = lireNonVide("Nom de l'animal > ");
        String maitre = lireNonVide("Nom du maitre > ");
        String race = lireNonVide("Race > ");
        String espece = lireNonVide("Espece > ");
        int vie = lireEntierPositif("Esperance de vie moyenne (annees) > ");
        String texteDossier = lireNonVide("Texte du dossier > ");
        PatientVue p = logique.enregistrer(nom, maitre, race, espece, vie, texteDossier);
        int total = logique.nombrePatients();
        afficher("Patient enregistre : " + decrire(p) + " ; total : " + total);
    }

    private static String decrire(PatientVue p) {
        return p.nom() + " (maitre : " + p.maitre() + ", race : " + p.race() + ", espece : " + p.espece() + ")";
    }

    private String lire(String prompt) {
        promptCourant = prompt;
        afficherSansSaut(prompt);
        try {
            String ligne = in.readLine();
            if (ligne == null) {
                throw new FinEntree();
            }
            return ligne;
        } catch (IOException e) {
            throw new FinEntree();
        } finally {
            promptCourant = "";
        }
    }

    private String lireNonVide(String prompt) {
        while (true) {
            String saisie = lire(prompt).trim();
            if (!saisie.isEmpty()) {
                return saisie;
            }
            afficher("Saisie vide, recommencez.");
        }
    }

    private int lireEntierPositif(String prompt) {
        while (true) {
            String saisie = lire(prompt).trim();
            try {
                int valeur = Integer.parseInt(saisie);
                if (valeur > 0) {
                    return valeur;
                }
                afficher("Entrez un nombre strictement positif.");
            } catch (NumberFormatException e) {
                afficher("\"" + saisie + "\" n'est pas un nombre entier.");
            }
        }
    }

    private synchronized void afficher(String s) {
        System.out.println(s);
    }

    private synchronized void afficherSansSaut(String s) {
        System.out.print(s);
        System.out.flush();
    }

    private synchronized void afficherAlerte(String message) {
        System.out.println();
        System.out.println("[ALERTE] " + message);
        System.out.print(promptCourant);
        System.out.flush();
    }

    private static class FinEntree extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
