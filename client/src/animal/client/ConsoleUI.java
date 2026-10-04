package animal.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ConsoleUI {

    private static class FinEntree extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private final ClientLogic logique;
    private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private volatile String promptCourant = "";

    public ConsoleUI(ClientLogic logique) { this.logique = logique; }

    public void lancer() {
        afficher("=== Cabinet veterinaire ===");
        try {
            boolean continuer = true;
            while (continuer) {
                afficherMenu();
                continuer = traiter(lire("Votre choix > ").trim());
            }
        } catch (FinEntree e) {
            afficher("Fin de l'entree : fermeture du client.");
        } finally {
            logique.fermer();   // se desabonne et desexporte l'observateur
        }
        afficher("Au revoir.");
    }

    private void afficherMenu() {
        afficher("");
        afficher("--- Menu (alertes : " + (logique.estAbonne() ? "abonne" : "non abonne") + ") ---");
        afficher("1. Lister les patients");
        afficher("2. Rechercher un patient par nom");
        afficher("3. Enregistrer un nouveau patient");
        afficher("4. Consulter le dossier d'un patient");
        afficher("5. Mettre a jour le dossier d'un patient");
        afficher("6. S'abonner aux alertes");
        afficher("7. Se desabonner des alertes");
        afficher("0. Quitter");
    }

    /** @return false pour quitter */
    private boolean traiter(String choix) {
        try {
            switch (choix) {
                case "1" -> lister();
                case "2" -> afficher(decrire(logique.rechercher(lireNonVide("Nom du patient > "))));
                case "3" -> enregistrer();
                case "4" -> {
                    String nom = lireNonVide("Nom du patient > ");
                    afficher("Dossier de " + nom + " : " + logique.lireDossier(nom));
                }
                case "5" -> {
                    String nom = lireNonVide("Nom du patient > ");
                    logique.modifierDossier(nom, lireNonVide("Nouveau texte du dossier > "));
                    afficher("Dossier mis a jour.");
                }
                case "6" -> { logique.sAbonner(this::afficherAlerte); afficher("Abonne aux alertes."); }
                case "7" -> { logique.seDesabonner(); afficher("Desabonne des alertes."); }
                case "0" -> { return false; }
                default -> afficher("Choix inconnu : \"" + choix + "\". Tapez un chiffre du menu.");
            }
        } catch (ClientException e) {
            afficher("Erreur : " + e.getMessage());
        }
        return true;
    }

    private void lister() throws ClientException {
        List<PatientVue> patients = logique.listerPatients();
        if (patients.isEmpty()) { afficher("Aucun patient."); return; }
        afficher(patients.size() + " patient(s) :");
        for (PatientVue p : patients) afficher(" - " + decrire(p));
    }

    private void enregistrer() throws ClientException {
        String nom = lireNonVide("Nom de l'animal > ");
        String maitre = lireNonVide("Nom du maitre > ");
        String race = lireNonVide("Race > ");
        String espece = lireNonVide("Espece > ");
        int vie = lireEntierPositif("Esperance de vie moyenne (annees) > ");
        String texteDossier = lireNonVide("texte dossier de l'animal > ");
        PatientVue p = logique.enregistrer(nom, maitre, race, espece, vie, texteDossier);
        afficher("Patient enregistre : " + decrire(p) + " ; total : " + logique.nombrePatients());
    }

    private static String decrire(PatientVue p) {
        return p.nom() + " (maitre : " + p.maitre() + ", race : " + p.race() + ", espece : " + p.espece() + ")";
    }

    // ---- saisies : validation, jamais d'exception pour l'utilisateur ----

    private String lire(String prompt) {
        promptCourant = prompt;
        afficherSansSaut(prompt);
        try {
            String ligne = in.readLine();
            if (ligne == null) throw new FinEntree();   // Ctrl+D / Ctrl+Z
            return ligne;
        } catch (IOException e) {
            throw new FinEntree();
        } finally {
            promptCourant = "";
        }
    }

    private String lireNonVide(String prompt) {
        while (true) {
            String s = lire(prompt).trim();
            if (!s.isEmpty()) return s;
            afficher("Saisie vide, recommencez.");
        }
    }

    private int lireEntierPositif(String prompt) {
        while (true) {
            String s = lire(prompt).trim();
            try {
                int v = Integer.parseInt(s);
                if (v > 0) return v;
                afficher("Entrez un nombre strictement positif.");
            } catch (NumberFormatException e) {
                afficher("\"" + s + "\" n'est pas un nombre entier.");
            }
        }
    }

    // ---- affichage : synchronise, car les alertes arrivent sur un thread RMI ----

    private synchronized void afficher(String s) { System.out.println(s); }

    private synchronized void afficherSansSaut(String s) { System.out.print(s); System.out.flush(); }

    /** Appelee par le thread RMI : alerte reperable, puis on reaffiche l'invite en cours. */
    private synchronized void afficherAlerte(String message) {
        System.out.println();
        System.out.println("[ALERTE] " + message);
        System.out.print(promptCourant);
        System.out.flush();
    }
}
