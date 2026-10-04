package animal.client;
/** Donnees d'un patient, sans aucun type RMI : c'est ce que voit l'interface. */
public record PatientVue(String nom, String maitre, String race, String espece) { }
