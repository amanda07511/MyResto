package iut.myresto.models;

/**
 * Created by amanda on 01/06/2017.
 */

public class Comment {
    private double note;
    private String message;
    private User user;
    private Resto resto;
    private String date;

    public Comment(double note, String message, User user, Resto resto, String date) {
        this.note = note;
        this.message = message;
        this.user = user;
        this.resto = resto;
        this.date = date;
    }

    public double getNote() {
        return note;
    }

    public void setNote(double note) {
        this.note = note;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Resto getResto() {
        return resto;
    }

    public void setResto(Resto resto) {
        this.resto = resto;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
