package src.Monsters;

import src.Game;

public class MonsterFactory {
    private static MonsterFactory instance;

    private MonsterFactory(){
    }

    /**
     * singleton monster factory global access point here
     */
    public static MonsterFactory getInstance(){
        if (instance == null) {
            instance = new MonsterFactory();
        }
        return instance;
    }

    /**
     * create new monster according to the given type and return
     */
    public Monster createMonster(char type, Game game) {
        return switch (type) {
            case 'g' -> new Troll(game);
            case 'h' -> new TX5(game);
            default -> null;
        };
    }
}
