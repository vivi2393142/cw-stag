package edu.uob;

import edu.uob.command.*;
import edu.uob.data.ActionData;
import edu.uob.data.EntityData;
import edu.uob.data.PathData;
import edu.uob.data.InteractableEntityData;
import edu.uob.entity.GameEntity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public final class GameServer {

    private static final char END_OF_TRANSMISSION = 4;
    private GameManager gameManager;

    public static void main(String[] args) throws IOException {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        GameServer server = new GameServer(entitiesFile, actionsFile);
        server.blockingListenOn(8888);
    }

    /**
     * Do not change the following method signature or we won't be able to mark your submission
     * Instanciates a new server instance, specifying a game with some configuration files
     *
     * @param entitiesFile The game configuration file containing all game entities to use in your game
     * @param actionsFile  The game configuration file containing all game actions to use in your game
     */
    public GameServer(File entitiesFile, File actionsFile) {
        // TODO: implement your server logic here
        // TODO: add GameManager, load from file
//        try {
        this.gameManager = new GameManager();
        GameLoader gameLoader = new GameLoader(gameManager);
        // gameLoader.load(entitiesFile, actionsFile);

        // TODO: get from file and remove below
        EntityData[] loadedLocations = {
                new EntityData("cabin", "A log cabin in the woods"),
                new EntityData("forest", "A dark forest"),
                new EntityData("cellar", "A dusty cellar"),
                new EntityData("storeroom", "Storage for any entities not placed in the game"),
        };
        PathData[] loadedPaths = {
                new PathData("cabin", "forest"),
                new PathData("forest", "cabin"),
                new PathData("cellar", "cabin")
        };
        InteractableEntityData[] loadedEntities = {
                new InteractableEntityData("axe", "A razor sharp axe", "cabin", GameEntity.EntityType.ARTEFACT),
                new InteractableEntityData("potion", "Magic potion", "cabin", GameEntity.EntityType.ARTEFACT),
                new InteractableEntityData("trapdoor", "Wooden trapdoor", "cabin", GameEntity.EntityType.FURNITURE),
                new InteractableEntityData("key", "Brass key", "forest", GameEntity.EntityType.ARTEFACT),
                new InteractableEntityData("tree", "A big tree", "forest", GameEntity.EntityType.FURNITURE),
                new InteractableEntityData("elf", "Angry Elf", "cellar", GameEntity.EntityType.CHARACTER),
                new InteractableEntityData("log", "A heavy wooden log", "storeroom", GameEntity.EntityType.ARTEFACT)
        };
        ActionData[] loadedActions = {
                new ActionData(
                        new HashSet<String>(Arrays.asList("open", "unlock")),
                        new HashSet<String>(Arrays.asList("trapdoor", "key")),
                        new HashSet<String>(List.of("key")),
                        new HashSet<String>(List.of("cellar")),
                        "You unlock the trapdoor and see steps leading down into a cellar"
                )
        };

        gameLoader.load(loadedLocations, loadedPaths, loadedEntities, loadedActions);

        Command commandInstance1 = new LookCommand("Vivi");
        System.out.println("[look]: " + commandInstance1.execute(this.gameManager));

        Command commandInstance7 = new InventoryCommand("Vivi");
        System.out.println("[inv]: " + commandInstance7.execute(this.gameManager));

        Command commandInstance2 = new GetCommand("Vivi", "axe");
        System.out.println("[get axe]: " + commandInstance2.execute(this.gameManager));

        Command commandInstance8 = new InventoryCommand("Vivi");
        System.out.println("[inv]: " + commandInstance8.execute(this.gameManager));

        Command commandInstance3 = new LookCommand("Vivi");
        System.out.println("[look]: " + commandInstance3.execute(this.gameManager));

        Command commandInstance4 = new GotoCommand("Vivi", "forest");
        System.out.println("[goto forest]: " + commandInstance4.execute(this.gameManager));

        Command commandInstance5 = new DropCommand("Vivi", "axe");
        System.out.println("[drop axe]: " + commandInstance5.execute(this.gameManager));

        Command commandInstance9 = new InventoryCommand("Vivi");
        System.out.println("[inv]: " + commandInstance9.execute(this.gameManager));

        Command commandInstance6 = new LookCommand("Vivi");
        System.out.println("[look]: " + commandInstance6.execute(this.gameManager));

//        } catch (Exception e) {
//            // TODO: handle init error
//            System.err.println(e.getMessage());
//        }
    }

    /**
     * Do not change the following method signature or we won't be able to mark your submission
     * This method handles all incoming game commands and carries out the corresponding actions.</p>
     *
     * @param command The incoming command to be processed
     */
    public String handleCommand(String command) {
        // TODO implement your server logic here
        // - receive an incoming command from a client
        // - process the actions that have been requested
        // - make changes to any game state that are required
        // - finally send a suitable response message back to the client

        // TODO: remove
        System.out.println("command: " + command);

//        Command commandInstance = Parser.parse(command);
//        return commandInstance.execute(this.gameManager);
        return "";
    }

    /**
     * Do not change the following method signature or we won't be able to mark your submission
     * Starts a *blocking* socket server listening for new connections.
     *
     * @param portNumber The port to listen on.
     * @throws IOException If any IO related operation fails.
     */
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.out.println("Connection closed");
                }
            }
        }
    }

    /**
     * Do not change the following method signature or we won't be able to mark your submission
     * Handles an incoming connection from the socket server.
     *
     * @param serverSocket The client socket to read/write from.
     * @throws IOException If any IO related operation fails.
     */
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
             BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            System.out.println("Connection established");
            String incomingCommand = reader.readLine();
            if (incomingCommand != null) {
                System.out.println("Received message from " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
