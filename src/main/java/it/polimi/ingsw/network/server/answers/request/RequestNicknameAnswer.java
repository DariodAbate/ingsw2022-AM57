package it.polimi.ingsw.network.server.answers.request;

import it.polimi.ingsw.network.server.answers.Answer;

public class RequestNicknameAnswer  implements Answer {
    private final String message;

    public RequestNicknameAnswer(String message) {
        this.message = message;
    }

    @Override
    public Object getMessage() {
        return message;
    }
}
