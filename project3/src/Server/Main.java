package Server;

public class Main {

    public static void main(String[] args) {
        Gui gui=new Gui();
        Server server=new Server();
        gui.setServerIntf(server);
        server.setGuiIntf(gui);
    }
}
