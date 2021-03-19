package code.entity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//Класс кнопки рестарта
public class RestartButton extends JButton {
    private final int width = 130;
    private final int height = 30;

    public RestartButton(Field field) {
        super();

        setText("Перезапуск");
        setBackground(Color.orange);
        setBounds(
                Field.COLUMNS_SIZE / 2 - 80,
                Field.COLUMNS_SIZE + 80,
                width,
                height
        );

        this.addActionListener(e -> field.restart());
    }
}
