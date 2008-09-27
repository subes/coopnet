package coopnetclient.frames.components;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextField;

public class AdvancedJTextField extends JTextField {

    private Color errorColor = Color.RED;
    private String lastValue = "";
    private Color foreGround;
    private boolean errorshowing = false;

    public AdvancedJTextField() {
        this.addFocusListener(focusAdapter);
        this.addKeyListener(keyListener);
    }

    public AdvancedJTextField(Color errorColor) {
        this();
        this.errorColor = errorColor;
    }

    @Override
    public String getText(){
        return errorshowing? lastValue : super.getText();
    }
    
    public void showErrorMessage(String errorMessage) {
        if (!errorshowing) {
            lastValue = getText();
            foreGround = getForeground();
            setForeground(errorColor);
            setText(errorMessage);
            errorshowing = true;
            repaint();
        }
        else{
            setText(errorMessage);
            repaint();
        }
    }
    private FocusAdapter focusAdapter = new FocusAdapter() {

        @Override
        public void focusGained(java.awt.event.FocusEvent evt) {
            if (errorshowing) {
                setForeground(foreGround);
                setText(lastValue);
                errorshowing = false;
                repaint();
            }
        }
    };
    private KeyListener keyListener = new KeyListener() {

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (errorshowing) {
                setForeground(foreGround);
                setText(lastValue);
                errorshowing = false;
                repaint();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    };
}
