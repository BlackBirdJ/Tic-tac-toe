package code.entity;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Field extends JFrame{
    public final static int COUNT_COL = 10;
    public final static int WINNER_LEN = 5;
    public final static int COLUMNS_SIZE = COUNT_COL * Column.COL_SIZE;
    public final static String cross = "X";
    public final static String zero = "0";

    private Column[][] columns = new Column[COUNT_COL][COUNT_COL];
    private int move = 1;
    private String winner = "";
    private ModeButton mode;
    private RestartButton restart_button;
    private boolean reverseLine = true;

    public Field() {
        super("Крестики-нолики " + COUNT_COL + "x" + COUNT_COL + " (комбинация из " + WINNER_LEN + ")");
        Dimension size = new Dimension(COLUMNS_SIZE + 120, COLUMNS_SIZE + 200);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(size);
        this.setLayout(null);
        this.getContentPane().setBackground(Color.DARK_GRAY);

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
        mode = new ModeButton();
        this.add(mode);

        System.out.println("Начата новая партия");
    }

    //Рестарт игры
    public void restart() {
        for (Column[] arr : columns)
            for (Column column : arr)
                column.clear();
        move = 1;
        winner = "";
        System.out.println("Начата новая партия");
    }

    //Обработка клика по игровой ячейке
    public void action(int row, int col) {
        if (!winner.isEmpty())
            return;
        if (this.columns[row][col].isEmpty()) {
            if (move % 2 != 0)
                this.columns[row][col].setText(cross);
            else
                this.columns[row][col].setText(zero);
            System.out.println("Player: " + this.columns[row][col].getText() + "\trow: " + row + "\tcol: " + col);
            move++;
        }
        checkWin();
        if (!winner.isEmpty()) {
            System.out.println("Победили: " + winner);
            return;
        }
        if (mode.isSelected() && move % 2 == 0)
            botAction(row, col);
    }

    //Проверка поля на победную комбинацию
    private void checkWin() {
        winner = "";
        Color colorWin = Color.green;
        try {
            //rows
            for (int i = 0; i < COUNT_COL; i++) {
                checkLine(0, i, 1, 0);
            }
            //columns
            for (int i = 0; i < COUNT_COL; i++)
                checkLine(i, 0, 0, 1);
            //left diagonals top-half
            for (int i = COUNT_COL - WINNER_LEN; i >= 0; i--) {
                checkLine(i, 0, 1, 1);
            }
            //left diagonals bot-half
            for (int i = COUNT_COL - WINNER_LEN; i > 0; i--) {
                checkLine( 0, i, 1, 1);
            }
            //right diagonals top-half
            for (int i = WINNER_LEN - 1; i < COUNT_COL; i++) {
                checkLine(i, 0, -1, 1);
            }
            //right diagonals bot-half
            for (int i = COUNT_COL - WINNER_LEN; i > 0; i--) {
                checkLine(COUNT_COL - 1, i, -1, 1);
            }
        } catch (GotWinnerException e) {
            winner = e.getWinnerType();
            for (Column column: e.getLinkedList())
                column.setBackground(colorWin);
        }
    }
    //Проверяет одну линию, на победную комбинацию
    private void checkLine(int xStart, int yStart, int xStep, int yStep) throws GotWinnerException {
        int count = 1;
        LinkedList<Column> linkedList = new LinkedList<>();
        linkedList.add(columns[yStart][xStart]);
        int i = yStart + yStep;
        int j = xStart + xStep;
        while (i < COUNT_COL && j < COUNT_COL && i >= 0 && j >= 0) {
            Column last = linkedList.getLast();
            Column column = columns[i][j];
            if (last.getText().equals(column.getText()) && !last.getText().isEmpty()) {
                linkedList.add(column);
                count++;
            }
            else {
                linkedList.removeIf(Objects::nonNull);
                linkedList.add(column);
                count = 1;
            }
            if (count == WINNER_LEN)
                throw new GotWinnerException(
                        linkedList.getLast().getText(),
                        linkedList
                );
            i += yStep;
            j += xStep;
        }
    }

    private void botAction(int row, int col) {
        Column column;
        if (move % 2 == 0)
            column = scanBotOnStep(zero, cross, row, col);
        else
            column = scanBotOnStep(cross, zero, row, col);
        if (column != null)
            action(column.getRow(), column.getCol());
    }


    /*
     * Функция возвращает ячейку, на которую стоит сходить
     * me - сторона за которую надо делать ход (cross|zero)
     * enemy - враг, которого надо попытаться заблокировать (cross|zero)
     */
    private Column scanBotOnStep(String me, String enemy, int row, int col) {
        Column column;
        if (move == 2) {
            Random random = new Random();
            while(true) {
                int i = random.nextInt(COUNT_COL);
                int j = random.nextInt(COUNT_COL);
                column = columns[i][j];
                if (column.isEmpty())
                    return column;
            }
        }
        //Попытка выиграть игру
        //scan rows
        for (int i = 0; i < COUNT_COL - 1; i++) {
            column = scanLineOnStep(0, i, 1, 0, WINNER_LEN - 1, me);
            if (column != null) {
                return column;
            }
        }
        //scan columns
        for (int i = 0; i < COUNT_COL - 1; i++) {
            column = scanLineOnStep(i, 0, 0, 1, WINNER_LEN - 1, me);
            if (column != null) {
                return column;
            }
        }
        //scan left diagonals top-half
        for (int i = 0; i <= COUNT_COL - WINNER_LEN; i++) {
            column = scanLineOnStep(i, 0, 1, 1, WINNER_LEN - 1, me);
            if (column != null) {
                return column;
            }
        }
        //scan left diagonals bot-half
        for (int i = 0; i <= COUNT_COL - WINNER_LEN; i++) {
            column = scanLineOnStep(0, i, 1, 1, WINNER_LEN - 1, me);
            if (column != null) {
                return column;
            }
        }
        //right diagonals top-half
        for (int i = WINNER_LEN - 1; i < COUNT_COL; i++) {
            column = scanLineOnStep(i, 0, -1, 1, WINNER_LEN - 1, me);
            if (column != null) {
                return column;
            }
        }
        //right diagonals bot-half
        for (int i = COUNT_COL - WINNER_LEN; i > 0; i--) {
            column = scanLineOnStep(COUNT_COL - 1, i, -1, 1, WINNER_LEN - 1, me);
            if (column != null) {
                return column;
            }
        }

        //Попытка заблокировать победу врага
        reverseLine = !reverseLine;
        //Определяем начало левой диагонали
        int leftYStart = row;
        int leftXStart = col;
        while (leftXStart != 0 && leftYStart != 0 ) {
            leftXStart -= 1;
            leftYStart -= 1;
        }
        //Определяем начало правой диагонали
        int rightYStart = row;
        int rightXStart = col;
        while (rightXStart != COUNT_COL-1 && rightYStart != 0) {
            rightXStart += 1;
            rightYStart -= 1;
        }
        //Проверяем только те линии, которые содержат последний ход врага
        for (int j = WINNER_LEN - 1; j >= WINNER_LEN - 2; j--) {
            //scan row
            column = scanLineOnStep(0, row, 1, 0, j, enemy);
            if (column != null) {
                return column;
            }
            //scan columns
            column = scanLineOnStep(col, 0, 0, 1, j, enemy);
            if (column != null) {
                return column;
            }
            //scan left diagonal
            column = scanLineOnStep(leftXStart, leftYStart, 1, 1, j, enemy);
            if (column != null) {
                return column;
            }
            //scan right diagonal
            column = scanLineOnStep(rightXStart, rightYStart, -1, 1, j, enemy);
            if (column != null) {
                return column;
            }
        }

        //Простой ход, если не удалось выиграть
        for (int j = WINNER_LEN - 2; j >= 1; j--) {
            //scan left diagonals top-half
            for (int i = 0; i <= COUNT_COL - WINNER_LEN; i++) {
                column = scanLineOnStep(i, 0, 1, 1, j, me);
                if (column != null) {
                    return column;
                }
            }
            //scan left diagonals bot-half
            for (int i = 0; i <= COUNT_COL - WINNER_LEN; i++) {
                column = scanLineOnStep(0, i, 1, 1, j, me);
                if (column != null) {
                    return column;
                }
            }
            //right diagonals top-half
            for (int i = WINNER_LEN - 1; i < COUNT_COL; i++) {
                column = scanLineOnStep(i, 0, -1, 1, j, me);
                if (column != null) {
                    return column;
                }
            }
            //right diagonals bot-half
            for (int i = COUNT_COL - WINNER_LEN; i > 0; i--) {
                column = scanLineOnStep(COUNT_COL - 1, i, -1, 1, j, me);
                if (column != null) {
                    return column;
                }
            }
            //scan rows
            for (int i = 0; i < COUNT_COL; i++) {
                column = scanLineOnStep(0, i, 1, 0, j, me);
                if (column != null) {
                    return column;
                }
            }
            //scan columns
            for (int i = 0; i < COUNT_COL; i++) {
                column = scanLineOnStep(i, 0, 0, 1, j, me);
                if (column != null) {
                    return column;
                }
            }
        }
        return null;
    }

    /*
     * Функция проверяет линию на возможность хода
     * xStart - координата начала линии по X
     * yStart - координата начала линии по Y
     * xStep - шаг по оси X
     * yStep - шаг по оси Y
     * player - тип подсчитываемых последовательных ячеек(cross|zero)
     */
    private Column scanLineOnStep(int xStart, int yStart, int xStep, int yStep, int count, String player) {
        int i = yStart;
        int j= xStart;
        LinkedList<Column> list = new LinkedList<>();
        while (i < COUNT_COL && j < COUNT_COL && i >= 0 && j >= 0) {
            list.add(columns[i][j]);
            i += yStep;
            j += xStep;
        }

        int k = 0;
        Column result = null;
        for (int l = 0; l < list.size(); j++) {
            Column column;
            //Если нужно обратить строку
            if (reverseLine)
                column = list.getLast();
            else
                column = list.getFirst();
            list.remove(column);
            //Если встретилась пустая ячейка, запоминаем ее
            if (column.isEmpty()) {
                result = column;
            }
            else
                //Если идет последовательность одинаковых непустых клеток, то считаем ее длину
                if (column.equals(player)) {
                    k++;
                }
                //Если встретилась другая клетка, то обнуляем счет
                else {
                    result = null;
                    k = 0;
                }
            //Если найдена оптимальная ячейка для хода, то возвращаем ее
            if (k == count && result != null) {
                return result;
            }
            i += yStep;
            j += xStep;
        }
        return null;
    }
}
