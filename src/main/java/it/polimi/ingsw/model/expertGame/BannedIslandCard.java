package it.polimi.ingsw.model.expertGame;

//TODO vedere logica con cui l' influenza viene calcolata = 0
public class BannedIslandCard extends ExpertCard {
    private final BannedIsland game;
    private final int islandIndex;

    public BannedIslandCard(int islandIndex, BannedIsland game) {
        super(2);
        this.islandIndex = islandIndex;
        this.game = game;
    }

    @Override
    public void effect() {
        if (!isPlayed()) {
            played = true;
            price += 1 ;
        }
        game.banIsland(islandIndex);
    }
}
