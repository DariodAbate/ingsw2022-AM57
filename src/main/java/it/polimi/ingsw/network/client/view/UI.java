package it.polimi.ingsw.network.client.view;

import it.polimi.ingsw.model.CardBack;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.network.client.modelBean.*;

import java.util.ArrayList;

/**
 * This class defines an interface used for both CLI and GUI. It contains the methods for drawing
 *
 * @author Dario d'Abate
 */
public interface  UI {

      void closeUserInterface();

      void displayGenericMessage(String message);
      void displaySelectableCardBack(ArrayList<CardBack> selectableCardBack);
      void displaySelectableTower(ArrayList<Tower> selectableTowers);
      void displayAllGame();
      void displayBoard(BoardBean board, boolean expertGame);
      void displayCard(PlayerBean playerBean);
      void displayArchipelago();
      void displayExpertCard();
      void displayClouds();
      void displayWinner(String winner);
}
