package code.entity;

import javax.swing.*;
import java.awt.*;

public class Column extends JButton {
    public final static int COL_SIZE = 60;
    private final Color background = Color.white;
    private final Font font = new Font("Arial", Font.PLAIN, 35);
    private final int marginLeft = 50;
    private final int marginTop = 40;
    private int row;
    private int col;

    public Column(int row, int col, Field field) {
        super();

        this.row = row;
        this.col = col;
        this.setFont(font);
        this.setBackground(background);
        this.setBounds(col * COL_SIZE + marginLeft, row * COL_SIZE + marginTop, COL_SIZE, COL_SIZE);

        this.addActionListener(e -> field.action(row, col));
    }

    public void clear() {
        this.setText("");
        this.setBackground(background);
    }

    public boolean isCross() {
        return this.getText().equals(Field.cross);
    }

    public boolean isZero() {
        return this.getText().equals(Field.zero);
    }

    public boolean isEmpty() {
        return getText().isEmpty();
    }

    public boolean equals(Column column) {
        return this.getText().equals(
                column.getText()
        );
    }

    public boolean equals(String text) {
        return this.getText().equals(text);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
