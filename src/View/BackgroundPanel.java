package View;

import javax.swing.*;
import java.awt.*;


class BackgroundPanel extends JPanel {
    // La classe BackgroundPanel sert à implémenter un JPanel qui peut afficher une image de fond
    private Image backgroundImage;


    public BackgroundPanel(String imagePath) {
        try {
            backgroundImage = new ImageIcon(imagePath).getImage();
            // Charger l'image
            setOpaque(false);
        } catch (Exception e) {
            e.printStackTrace();
            // Imprimer la trace de l'exception en cas d'erreur
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);// Appeler la méthode paintComponent de la classe parente

        if (backgroundImage != null) {
            // Dessiner l'image de fond pour couvrir tout le panneau
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }

    }
}
