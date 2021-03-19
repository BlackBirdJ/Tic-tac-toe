package code.entity;

import java.util.HashMap;
import java.util.LinkedList;

public class GotWinnerException extends Exception{
    private String winnerType;
    private LinkedList<Column> linkedList;

    public GotWinnerException(String winnerType, LinkedList<Column> linkedList) {
        this.winnerType = winnerType;
        this.linkedList = linkedList;
    }

    public String getWinnerType() {
        return winnerType;
    }

    public LinkedList<Column> getLinkedList() {
        return linkedList;
    }
}
