package infra.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;

public class Input extends JPanel {

    JTextField textField;

    public Input(String label) {
        JLabel labelPainel = new JLabel(label);
        textField = new JTextField("");
        this.setLayout(new BorderLayout());
        this.add(labelPainel,BorderLayout.WEST);
        this.add(textField,BorderLayout.CENTER);
    }

    public void setText(String text) {
        this.textField.setText(text);
    }
    public String getText() {
        return this.textField.getText();
    }
    @Override
    public synchronized void addMouseListener(MouseListener l) {
        this.textField.addMouseListener(l);
    }
}
