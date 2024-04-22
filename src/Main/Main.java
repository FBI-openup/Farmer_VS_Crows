package Main;

import View.GameInterface;
import javax.swing.*;


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameInterface::new);
    }
}