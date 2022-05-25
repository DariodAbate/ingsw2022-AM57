package it.polimi.ingsw.network.client.view;

import it.polimi.ingsw.model.AssistantCard;
import it.polimi.ingsw.model.CardBack;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.network.client.modelBean.*;
import it.polimi.ingsw.network.client.modelBean.ExpertCard.ExpertCardBean;
import it.polimi.ingsw.network.server.answers.*;
import it.polimi.ingsw.network.server.answers.update.*;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

/**
 * This class defines an abstract class used for both CLI and GUI. It contains the model view as a Java bean.
 * It is updated according to the state of the server side model
 *
 * @author Dario d'Abate
 */
public abstract class UI {
     protected GameBean gameBean; //model view
     protected String nickname;

     /**
      * This method will show content based on the event that the client receives, thus it updates the view
      * @param evt event occurred due to server
      */
     public void propertyChange(PropertyChangeEvent evt) {
          switch (evt.getPropertyName()) {
               case "stopSending" -> closeUserInterface();
               case "genericMessage" -> displayGenericMessage((String)evt.getNewValue());
               case "towerChoice" -> displaySelectableTower((ArrayList<Tower>) evt.getNewValue());
               case "cardBackChoice" -> displaySelectableCardBack((ArrayList<CardBack>) evt.getNewValue());
               case "nickname" -> this.nickname = (String) evt.getNewValue();
               case "gameState" -> {
                    this.gameBean = (GameBean)evt.getNewValue();
                    displayAllGame() ;
               }
               case "cardPlayed" -> {
                    String nickname = ((AssistantCardPlayedAnswer)evt.getNewValue()).getNickname();
                    ArrayList<AssistantCard> hand = ((AssistantCardPlayedAnswer)evt.getNewValue()).getHand();
                    AssistantCard playedCard = ((AssistantCardPlayedAnswer)evt.getNewValue()).getCard();

                    for(PlayerBean player :gameBean.getPlayers()){
                         if(player.getNickname().equals(nickname)){
                              player.setPlayedCard(playedCard);
                              player.setHand(hand); //new hand
                         }
                    }
                    displayAllGame() ;
               }
               case "toHall" -> {
                    String nickname = ((ToHallUpdateAnswer)evt.getNewValue()).getNickname();
                    BoardBean updatedBoard = ((ToHallUpdateAnswer)evt.getNewValue()).getUpdatedBoard();

                    for(PlayerBean player :gameBean.getPlayers()){
                         if(player.getNickname().equals(nickname))
                              player.setBoard(updatedBoard);
                    }
                    displayAllGame() ;
               }
               case "toIsland" -> {
                    String nickname = ((ToIslandUpdateAnswer)evt.getNewValue()).getNickname();
                    BoardBean updatedBoard = ((ToIslandUpdateAnswer)evt.getNewValue()).getUpdatedBoard();
                    ArrayList<IslandBean> updatedArchipelago = ((ToIslandUpdateAnswer)evt.getNewValue()).getUpdatedArchipelago();

                    for(PlayerBean player :gameBean.getPlayers()){
                         if(player.getNickname().equals(nickname))
                              player.setBoard(updatedBoard);
                    }
                    gameBean.setArchipelago(updatedArchipelago);
                    displayAllGame() ;
               }

               case "motherMovement" -> {
                    int motherNature =  ((MotherNatureUpdateAnswer)evt.getNewValue()).getUpdatedMotherNature();
                    ArrayList<BoardBean> updatedBoards = ((MotherNatureUpdateAnswer)evt.getNewValue()).getUpdatedBoards();
                    ArrayList<IslandBean> updatedArchipelago = ((MotherNatureUpdateAnswer)evt.getNewValue()).getUpdatedArchipelago();

                    for(int i = 0; i < gameBean.getPlayers().size(); i++){
                         gameBean.getPlayers().get(i).setBoard(updatedBoards.get(i));
                    }
                    gameBean.setMotherNature(motherNature);
                    gameBean.setArchipelago(updatedArchipelago);
                    displayAllGame() ;
               }
               case "cloudChoice" -> {
                    ArrayList<BoardBean> updatedBoards = ((CloudsUpdateAnswer)evt.getNewValue()).getUpdatedBoards();
                    ArrayList<CloudBean> updateClouds = ((CloudsUpdateAnswer)evt.getNewValue()).getUpdateClouds();

                    for(int i = 0; i < gameBean.getPlayers().size(); i++){
                         gameBean.getPlayers().get(i).setBoard(updatedBoards.get(i));
                    }
                    gameBean.setCloudTiles(updateClouds);
                    displayAllGame();
               }
               case "expertCard" -> {
                    ArrayList<BoardBean> updatedBoards = ((ExpertCardUpdateAnswer)evt.getNewValue()).getUpdatedBoards();
                    ArrayList<IslandBean> updatedArchipelago = ((ExpertCardUpdateAnswer)evt.getNewValue()).getUpdatedArchipelago();
                    ArrayList<ExpertCardBean> updatedExpertCards = ((ExpertCardUpdateAnswer)evt.getNewValue()).getUpdatedExpertCards();

                    gameBean.setExpertCards(updatedExpertCards);
                    if(updatedBoards != null){
                         for(int i = 0; i < gameBean.getPlayers().size(); i++){
                              gameBean.getPlayers().get(i).setBoard(updatedBoards.get(i));
                         }
                    }
                    if(updatedArchipelago != null){
                         gameBean.setArchipelago(updatedArchipelago);
                    }
                    displayAllGame();
               }
          }
     }

     abstract void closeUserInterface();

     abstract void displayGenericMessage(String message);
     abstract void displaySelectableCardBack(ArrayList<CardBack> selectableCardBack);
     abstract void displaySelectableTower(ArrayList<Tower> selectableTowers);
     abstract void displayAllGame();
     abstract void displayBoard(BoardBean board, boolean expertGame);
     abstract void displayCard(PlayerBean playerBean);
     abstract void displayArchipelago();
     abstract void displayExpertCard();
     abstract void displayClouds();
}
