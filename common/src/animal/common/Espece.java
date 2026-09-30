package animal.common;

import java.io.Serializable;

public class Espece implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nom;
    private int dureeDeVieMoyenne;
    public Espece(String nom, int dureeDeVieMoyenne){
        this.nom = nom;
        this.dureeDeVieMoyenne = dureeDeVieMoyenne;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public int getDureeDeVieMoyenne() {
        return dureeDeVieMoyenne;
    }
    public void setDureeDeVieMoyenne(int dureeDeVieMoyenne) {
        this.dureeDeVieMoyenne = dureeDeVieMoyenne;
    }
    
    @Override 
    public String toString(){
        return nom + " (esperance de vie : " + dureeDeVieMoyenne + " ans)" ;
    }   
}
