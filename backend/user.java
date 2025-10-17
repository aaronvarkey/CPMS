package backend;

public class user {
    private String name;
    private String id;
    private String password;
    private String role;

    public user(String id, String name, String password, String role) {
        this.name = name;
        this.id = id;
        this.password = password;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public boolean checkPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }
}
