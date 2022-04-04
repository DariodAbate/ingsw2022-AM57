package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.Collections;

public class Round {
    private int roundNumber;
    private ArrayList<Player> pianificationOrder;
    private ArrayList<Player> actionOrder;
    private ArrayList<Player> playersCopy;
    private Player currentTurn;
    private boolean isPianification;


    public Round(ArrayList<Player> players) {
        if (players == null)
            throw new NullPointerException();
        if (players.size() < 2 || players.size() > 3)
            throw new IllegalArgumentException("You need between 2 and 3 players");
        roundNumber = 0;
        playersCopy = new ArrayList<>(players);
        actionOrder = new ArrayList<>(players);
        pianificationOrder = new ArrayList<>(players);
        isPianification = true;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public ArrayList<Player> getPianificationOrder() {
        if (pianificationOrder == null)
            throw new NullPointerException("There is no pianification list yet");
        else
            return pianificationOrder;
    }

    public ArrayList<Player> getActionOrder() {
        if (actionOrder == null)
            throw new NullPointerException("There is no action list yet");
        else
            return actionOrder;
    }

    public void setPianificationOrder() {
        pianificationOrder.removeAll(playersCopy);
        Player firstPlayer = Collections.min(playersCopy, (player1, player2) -> {
            if (player1.viewLastCard().getPriority() < player2.viewLastCard().getPriority())
                return -1;
            else
                return 1;
        });
        pianificationOrder.add(firstPlayer);
        int firstPlayerIndex = playersCopy.indexOf(firstPlayer);
        if (playersCopy.size() == 3) {
            pianificationOrder.add(playersCopy.get((firstPlayerIndex +1)%3));
            pianificationOrder.add(playersCopy.get((firstPlayerIndex +2)%3));
        }
        if(playersCopy.size() == 2) {
            pianificationOrder.add(playersCopy.get((firstPlayerIndex +1)%2));
        }
    }

    // Case of same priority card to be added
    public void setActionOrder() {
        for (Player player : playersCopy) {
            Collections.sort(actionOrder, (player1, player2) -> {
                if (player1.viewLastCard().getPriority() < player2.viewLastCard().getPriority())
                    return -1;
                else
                    return 1;
            });
        }
        isPianification = false;
    }

    public Player getCurrentPlayer() {
        return currentTurn;
    }

    public void nextTurn() {
        if (isPianification) {
            if (pianificationOrder.iterator().hasNext())
                currentTurn = pianificationOrder.iterator().next();
            else
                throw new IndexOutOfBoundsException("End of pianificationOrder list");
        } else {
            if (actionOrder.iterator().hasNext())
                currentTurn = actionOrder.iterator().next();
            else
                throw new IndexOutOfBoundsException("End of actionOrder list");
        }
    }

    public void nextRound() {
        roundNumber += 1;
        isPianification = true;
    }
}

