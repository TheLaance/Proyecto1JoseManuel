package data.entity;

import java.util.List;

public class Group {
    private int id;
    private String nameGroup;
    private List<Message> message;

    public Group() {
    }

    public Group(int id, String nameGroup, List<Message> message) {
        this.id = id;
        this.nameGroup = nameGroup;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameGroup() {
        return nameGroup;
    }

    public void setNameGroup(String nameGroup) {
        this.nameGroup = nameGroup;
    }

    public List<Message> getMessage() {
        return message;
    }

    public void setMessage(List<Message> message) {
        this.message = message;
    }
}
