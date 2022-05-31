package it.polimi.ingsw.network.server.answers.request;

import it.polimi.ingsw.network.server.answers.Answer;

public class StartAnswer implements Answer {
    private final String message;

    public StartAnswer(String message) {
        this.message = message;
    }

    @Override
    public Object getMessage() {
        return message;
    }
}
