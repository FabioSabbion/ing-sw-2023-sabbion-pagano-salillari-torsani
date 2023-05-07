package it.polimi.ingsw;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.events.ViewEvent;
import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.models.*;
import it.polimi.ingsw.utils.Observer;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        PersonalGoalCard temp = new PersonalGoalCard(new ArrayList<>(Arrays.asList(
                new ImmutablePair<>(Category.GAMES, new Coordinates(0, 0)),
                new ImmutablePair<>(Category.BOOKS, new Coordinates(1, 1)),
                new ImmutablePair<>(Category.PLANTS, new Coordinates(2, 2))
        )
        )
        );
        String[] players = {
                "Andri", "LP"
        };

        GameController controller = new GameController(List.of(players));

        controller.game.addObserver(new Observer<GameUpdate, ViewEvent>() {
            @Override
            public void update(GameUpdate value, ViewEvent eventType) {
                System.out.println("NEW UPDATE");
                System.out.println(value);
            }
        });
    }
}
