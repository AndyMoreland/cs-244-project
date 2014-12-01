package gameengine;

/**
 * Created by andrew on 11/30/14.
 */
public class Player {
    private String name;
    private boolean active;

    public Player(String name) {
        this.name = name;
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

    private Player(String name, boolean active){
        this.name = name;
        this.active = active;
    }

    public static Player makeInactivePlayer(){
        return new Player("[Inactive]", false);
    }
}
