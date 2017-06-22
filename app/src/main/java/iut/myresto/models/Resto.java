package iut.myresto.models;

/**
 * Created by amanda on 30/05/2017.
 */

public class Resto {
    private  int id;
    private String nom;
    private String type;
    private double lat;
    private double lng;
    private double note;
    private String photo;
    private User user;

    public Resto(int id,String nom, String type, double lat, double note, double lng, String photo, User user) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.lat = lat;
        this.note = note;
        this.lng = lng;
        this.photo = photo;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getNote() {
        return note;
    }

    public void setNote(double note) {
        this.note = note;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

}
