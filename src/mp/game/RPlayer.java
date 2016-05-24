package mp.game;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import java.util.Random;

/** Un giocatore per Find-Treasure che fa mosse random */
public class RPlayer implements Player {
    public RPlayer() {
        shape = new ImageView(getClass().getResource("ghostRed.png").toString());
    }
    @Override
    public void play(Console c) {
        Random rnd = new Random();
        while (true) {
            State s = c.move(Dir.values()[rnd.nextInt(Dir.values().length)]);
            if (State.GAME_OVER.equals(s) || State.WIN.equals(s)) break;
        }
    }

    @Override
    public Node getNode() { return shape; }

    private Node shape;
}