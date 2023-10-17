package data.entity;

import java.sql.Timestamp;

public class Message {
    private int id;
    private User user_id;
    private String message;
    private Timestamp timestamp;

    public Message() {
    }


    public Message(int id, User user_id, String message, Timestamp timestamp) {
        this.id = id;
        this.user_id = user_id;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser_id() {
        return user_id;
    }

    public void setUser_id(User user_id) {
        this.user_id = user_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }


}
