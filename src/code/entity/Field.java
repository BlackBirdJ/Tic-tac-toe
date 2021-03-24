package code.entity;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Field extends JFrame{
    public final static int COUNT_COL = 7;
    public final static int COLUMNS_SIZE = COUNT_COL * Column.COL_SIZE;
    public final static String cross = "X";
    public final static String zero = "0";
    public final static int WINNER_LEN = 5; // > 1 and <= COUNT_COL

    private Column[][] columns = new Column[COUNT_COL][COUNT_COL];
    private int move = 1;
    private String winner = "";
    private ModeButton mode;
    private RestartButton restart_button;

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

    //Обработка клика по игровой ячейке
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
            return;
        }
//        computerAction();
        if (mode.isSelected() && move % 2 == 0) {
            botAction();
        }
    }

    public void computerAction() {
        boolean fl = false;
        while (!fl) {
            Random random = new Random();
            int i = random.nextInt(COUNT_COL - 1);
            int j = random.nextInt(COUNT_COL - 1);
            if (columns[i][j].isEmpty()) {
                action(i, j);
                fl = true;
            }
        }
    }

    //Проверка поля на победную комбинацию
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
            int leftSize = WINNER_LEN;
            for (int i = COUNT_COL - WINNER_LEN; i >= 0; i--) {
                checkLine(i, 0, 1, 1, leftSize);
                leftSize++;
            }
            //left diagonal bot-half
            leftSize = WINNER_LEN;
            for (int i = COUNT_COL - WINNER_LEN; i > 0; i--) {
                checkLine( 0, i, 1, 1, leftSize);
                leftSize++;
            }
            //right diagonal top-half
            int rightSize = WINNER_LEN;
            for (int i = WINNER_LEN - 1; i < COUNT_COL; i++) {
                checkLine(i, 0, -1, 1, rightSize);
                rightSize++;
            }
            //right diagonal bot-half
            rightSize = WINNER_LEN;
            for (int i = COUNT_COL - WINNER_LEN; i > 0; i--) {
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

    //Рестарт игры
    public void restart() {
        for (Column[] arr : columns)
            for (Column column : arr)
                column.clear();
        move = 1;
        winner = "";
        System.out.println("Начата новая партия");
    }

    public void botAction() {
        Column column;
        if (move % 2 == 0)
            column = scanBotOnStep(zero, cross);
        else
            column = scanBotOnStep(cross, zero);
        if (column != null)
            action(column.getRow(), column.getCol());
    }


    /*
     * Функция возвращает ячейку, на которую стоит сходить
     * me - сторона за которую надо делать ход (cross|zero)
     * enemy - враг, которого надо попытаться заблокировать (cross|zero)
     */
    public Column scanBotOnStep(String me, String enemy) {
        //Если свободен центр на втором ходе
        if (move == 2) {
            Column column = columns[COUNT_COL / 2][COUNT_COL / 2];
            if (column.isEmpty())
                return column;
            column = columns[COUNT_COL / 2 - 1][COUNT_COL / 2 - 1];
            if (column.isEmpty())
                return column;
        }
        Column column = null;
        //Попытка выиграть игру
        //scan rows
        for (int i = 0; i < COUNT_COL - 1; i++) {
            column = scanLineOnStep(0, i, 1, 0, COUNT_COL - 1, WINNER_LEN - 1, me, false);
            if (column != null) {
                return column;
            }
        }
        //scan columns
        for (int i = 0; i < COUNT_COL - 1; i++) {
            column = scanLineOnStep(i, 0, 0, 1, COUNT_COL - 1, WINNER_LEN - 1, me, false);
            if (column != null) {
                return column;
            }
        }
        //scan left diagonal top-half
        int leftSize = WINNER_LEN;
        for (int i = COUNT_COL - WINNER_LEN; i >= 0; i--) {
            column = scanLineOnStep(0, 0, 1, 1, leftSize, WINNER_LEN - 1, me, false);
            if (column != null) {
                return column;
            }
            leftSize++;
        }
        //scan left diagonal bot-half
        leftSize = WINNER_LEN;
        for (int i = COUNT_COL - WINNER_LEN; i > 0; i--) {
            column = scanLineOnStep(0, i, 1, 1, leftSize, WINNER_LEN - 1, me, false);
            if (column != null) {
                return column;
            }
            leftSize++;
        }
        //right diagonal top-half
        int rightSize = WINNER_LEN;
        for (int i = WINNER_LEN - 1; i < COUNT_COL; i++) {
            column = scanLineOnStep(i, 0, -1, 1, rightSize, WINNER_LEN - 1, me, false);
            if (column != null) {
                return column;
            }
            rightSize++;
        }
        //right diagonal bot-half
        rightSize = WINNER_LEN;
        for (int i = COUNT_COL - WINNER_LEN; i > 0; i--) {
            column = scanLineOnStep(COUNT_COL - 1, i, -1, 1, rightSize, WINNER_LEN - 1, me, false);
            if (column != null) {
                return column;
            }
            rightSize++;
        }

        //Попытка заблокировать победу врага
        for (int j = WINNER_LEN - 1; j > 2; j--) {
            //scan left diagonal top-half
            leftSize = WINNER_LEN;
            for (int i = COUNT_COL - WINNER_LEN; i >= 0; i--) {
                column = scanLineOnStep(0, 0, 1, 1, leftSize, j, enemy, false);
                if (column != null) {
                    return column;
                }
                leftSize++;
            }
            //scan left diagonal bot-half
            leftSize = WINNER_LEN;
            for (int i = COUNT_COL - WINNER_LEN; i > 0; i--) {
                column = scanLineOnStep(0, i, 1, 1, leftSize, j, enemy, false);
                if (column != null) {
                    return column;
                }
                leftSize++;
            }
            //right diagonal top-half
            rightSize = WINNER_LEN;
            for (int i = WINNER_LEN - 1; i < COUNT_COL; i++) {
                column = scanLineOnStep(i, 0, -1, 1, rightSize, j, enemy, false);
                if (column != null) {
                    return column;
                }
                rightSize++;
            }
            //right diagonal bot-half
            rightSize = WINNER_LEN;
            for (int i = COUNT_COL - WINNER_LEN; i > 0; i--) {
                column = scanLineOnStep(COUNT_COL - 1, i, -1, 1, rightSize, j, enemy, false);
                if (column != null) {
                    return column;
                }
                rightSize++;
            }
            //scan rows
            for (int i = 0; i < COUNT_COL; i++) {
                column = scanLineOnStep(0, i, 1, 0, COUNT_COL, j, enemy, false);
                if (column != null) {
                    return column;
                }
            }
            //scan columns
            for (int i = 0; i < COUNT_COL ; i++) {
                column = scanLineOnStep(i, 0, 0, 1, COUNT_COL, j, enemy, false);
                if (column != null) {
                    return column;
                }
            }
        }

        //Простой ход, если не удалось выиграть
        for (int j = WINNER_LEN; j >= 1; j--) {
            for (int i = 0; i < COUNT_COL - 1; i++) {
                column = scanLineOnStep(0, i, 1, 0, COUNT_COL - 1, j, me, false);
                if (column != null) {
                    return column;
                }
            }
            //scan columns
            for (int i = 0; i < COUNT_COL - 1; i++) {
                column = scanLineOnStep(i, 0, 0, 1, COUNT_COL - 1, j, me, false);
                if (column != null) {
                    return column;
                }
            }
            //scan left diagonal top-half
            leftSize = WINNER_LEN;
            for (int i = COUNT_COL - WINNER_LEN; i >= 0; i--) {
                column = scanLineOnStep(0, 0, 1, 1, leftSize, j, me, false);
                if (column != null) {
                    return column;
                }
                leftSize++;
            }
            //scan left diagonal bot-half
            leftSize = WINNER_LEN;
            for (int i = COUNT_COL - WINNER_LEN; i > 0; i--) {
                column = scanLineOnStep(0, i, 1, 1, leftSize, j, me, false);
                if (column != null) {
                    return column;
                }
                leftSize++;
            }
            //right diagonal top-half
            rightSize = WINNER_LEN;
            for (int i = WINNER_LEN - 1; i < COUNT_COL; i++) {
                column = scanLineOnStep(i, 0, -1, 1, rightSize, j, me, false);
                if (column != null) {
                    return column;
                }
                rightSize++;
            }
            //right diagonal bot-half
            rightSize = WINNER_LEN;
            for (int i = COUNT_COL - WINNER_LEN; i > 0; i--) {
                column = scanLineOnStep(COUNT_COL - 1, i, -1, 1, rightSize, j, me, false);
                if (column != null) {
                    return column;
                }
                rightSize++;
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
     * sizeLine - размер линии(количество клеток в ней)
     * player - тип подсчитываемых последовательных ячеек(cross|zero)
     * countEmpty - логическая переменная, отвечает за подсчет пустых ячеек(считать их или нет), нужна для выгодного хода, когдв не удалось заблокировать или выиграть
     */
    public Column scanLineOnStep(int xStart, int yStart, int xStep, int yStep, int sizeLine, int count, String player, boolean countEmpty) {
        int k = 0;
        Column result = null;
        int i = yStart;
        int j = xStart;

        for (int c = 0; c < sizeLine; c++) {
            Column column = columns[i][j];
            //Если встретилась пустая ячейка, запоминаем ее
            if (column.isEmpty()) {
                result = column;
                //Если нужно их считать
                if (countEmpty)
                    k++;
            }
            else
                //Если идет последовательность одинаковых непустых клеток, то считаем ее длину
                if (column.equals(player)) {
                    k++;
                }
                //Если встретилась другая клетка, то обнуляем счет
                else {
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
