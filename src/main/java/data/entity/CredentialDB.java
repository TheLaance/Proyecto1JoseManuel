package data.entity;

public class CredentialDB {
    private String username;
    private String password;
    private String db;

    public CredentialDB() {
    }

    public CredentialDB(String username, String password, String db) {
        this.username = username;
        this.password = password;
        this.db = db;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }
}
