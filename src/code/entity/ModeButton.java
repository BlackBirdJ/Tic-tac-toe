package code.entity;

import javax.swing.*;

public class ModeButton extends JCheckBox {
    public ModeButton() {
        super("Игра с ботом");
        setBounds(Field.COLUMNS_SIZE / 2 + 82, Field.COLUMNS_SIZE + 83, 100, 25);
        setSelected(true);
    }
}
