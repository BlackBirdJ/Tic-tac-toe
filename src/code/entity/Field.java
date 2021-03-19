package code.entity;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Field extends JFrame{
    public final static int COUNT_COL = 10;
    public final static int COLUMNS_SIZE = COUNT_COL * Column.COL_SIZE;
    public final static String cross = "X";
    public final static String zero = "0";

    private Column[][] columns = new Column[COUNT_COL][COUNT_COL];
    private int move = 1;
    private String winner = "";
    private ModeButton mode;
    private RestartButton restart_button;

    public Field() {
        super("Крестики-нолики");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(COLUMNS_SIZE + 120, COLUMNS_SIZE + 200);
        this.setLayout(null);

        //Добавление полей на экран
        for (int i = 0; i < COUNT_COL; i++)
            for (int j = 0; j < COUNT_COL; j++) {
                columns[i][j] = new Column(i, j, this);
                this.add(columns[i][j]);
            }

        //Инициализация кнопки рестарта
        restart_button = new RestartButton(this);
        this.add(restart_button);

        //Кнопка выбора режима игры
//        mode = new ModeButton();
//        this.add(mode);

        this.getContentPane().setBackground(
                Color.darkGray
        );

        System.out.println("Начата новая партия");
    }

    public void action(int row, int col) {
        if (!winner.isEmpty())
            return;
        if (this.columns[row][col].isEmpty()) {
            if (move % 2 != 0) {
                this.columns[row][col].setText(cross);
            }
            else {
                this.columns[row][col].setText(zero);
            }
            System.out.println("Player: " + this.columns[row][col].getText() + "\trow: " + row + "\tcol: " + col);
            move++;
        }
        checkWin();
        if (!winner.isEmpty()) {
            System.out.println("Победили: " + winner);
        }
    }

    public void checkWin() {
        winner = "";
        Color colorWin = Color.green;
        try {
            //rows
            for (int i = 0; i < COUNT_COL; i++) {
                checkLine(0, i, 1, 0, COUNT_COL);
            }
            //col
            for (int i = 0; i < COUNT_COL; i++)
                checkLine(i, 0, 0, 1, COUNT_COL);
            //left diagonal top-half
            int leftSize = 3;
            for (int i = COUNT_COL - 3; i >= 0; i--) {
                checkLine(i, 0, 1, 1, leftSize);
                leftSize++;
            }
            //left diagonal bot-half
            leftSize = 3;
            for (int i = COUNT_COL - 3; i > 0; i--) {
                checkLine( 0, i, 1, 1, leftSize);
                leftSize++;
            }
            //right diagonal top-half
            int rightSize = 3;
            for (int i = 2; i < COUNT_COL; i++) {
                checkLine(i, 0, -1, 1, rightSize);
                rightSize++;
            }
            //right diagonal bot-half
            rightSize = 3;
            for (int i = COUNT_COL - 3; i > 0; i--) {
                checkLine(COUNT_COL - 1, i, -1, 1, rightSize);
                rightSize++;
            }
        } catch (GotWinnerException e) {
            winner = e.getWinnerType();
            for (Column column: e.getLinkedList())
                column.setBackground(colorWin);
        }
    }
    //Проверяет одну линию, на победную комбинацию
    public void checkLine(int xStart, int yStart, int xStep, int yStep, int size) throws GotWinnerException {
        int count = 1;
        LinkedList<Column> linkedList = new LinkedList<>();
        linkedList.add(columns[yStart][xStart]);
        int i = yStart + yStep;
        int j = xStart + xStep;
        for (int k = 1; k < size; k++) {
            Column last = linkedList.getLast();
            Column column = columns[i][j];
            if (last.getText().equals(column.getText()) && !last.getText().isEmpty()) {
                linkedList.add(column);
                count++;
            }
            else {
                count = 1;
                linkedList.removeIf(Objects::nonNull);
                linkedList.add(column);
            }
            if (count == 3)
                throw new GotWinnerException(
                        linkedList.getLast().getText(),
                        linkedList
                );
            i += yStep;
            j += xStep;
        }
    }

    public void restart() {
        for (Column arr[] : columns)
            for (Column cell : arr)
                cell.clear();
        move = 1;
        winner = "";
        System.out.println("Начата новая партия");
    }
}
