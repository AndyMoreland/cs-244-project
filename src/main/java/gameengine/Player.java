package gameengine;

/**
 * Created by andrew on 11/30/14.
 */
public class Player {
    private final int id;
    private String name;
    private boolean active;

    public Player(String name, int id) {
        this.name = name;
        this.id = id;
        this.active = true;
    }

    public String getName() {
        return name;
    }
    public boolean isActive() { return active; }

    public void activate(String name){
        this.name = name;
        this.active = true;
    }

    public void deactivate(){
        this.active = false;
    }

    private Player(String name, int id, boolean active){
        this.name = name;
        this.id = id;
        this.active = active;
    }

    public static Player makeInactivePlayer(){
        return new Player("[Inactive]", -1, false);
    }
}
