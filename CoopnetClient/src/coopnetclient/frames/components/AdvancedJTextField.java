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
    private boolean errorShowing = false;

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
        return errorShowing? lastValue : super.getText();
    }
    
    @Override
    public void setText(String t) {
        if (errorShowing) {
            lastValue = t;
            setForeground(foreGround);
            errorShowing = false;
            super.setText(lastValue);            
            repaint();
        } else {
            super.setText(t);
        }
    }
    
    public void showErrorMessage(String errorMessage) {
        if (!errorShowing) {
            lastValue = getText();
            foreGround = getForeground();
            setForeground(errorColor);
            super.setText(errorMessage);
            errorShowing = true;
            repaint();
        }
        else{
            super.setText(errorMessage);
            setForeground(errorColor);
            repaint();
        }
    }
    private FocusAdapter focusAdapter = new FocusAdapter() {

        @Override
        public void focusGained(java.awt.event.FocusEvent evt) {
            if (errorShowing) {
                setForeground(foreGround);
                setText(lastValue);                
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
            if (errorShowing) {
                setForeground(foreGround);
                setText(lastValue);                
                repaint();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    };
}
