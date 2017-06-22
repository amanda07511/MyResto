package iut.myresto.models;

/**
 * Created by amanda on 30/05/2017.
 */

public class User {
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String photo;

    public User(String nom, String prenom, String email, String password, String photo) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.photo = photo;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
