import it.polimi.ingsw.GamePersistence;

public class Test {
    public static void main(String[] args) {
        GamePersistence gamePersistence = new GamePersistence();

        gamePersistence.loadOldGames();
    }
}
