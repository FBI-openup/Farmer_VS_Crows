package View;

import Controller.FarmerMouseListener;
import Model.*;
import Model.Centers.SeedConversionCenter;
import Model.Centers.SellingCenter;
import Model.Units.Corns.Corn;
import Model.Units.Scarecrows.Scarecrow;
import Model.Units.Units;
import Model.Units.Crows.Crow;
import Model.Units.Famers.Farmer;

import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;


public class GamePanel extends JPanel {

    // SCREEN SETTINGS
    private final int originalTileSize = 16; // 16x16 pixels
    private final int scale = 3; // 3x scale

    private final int tileSize = originalTileSize * scale; // 48x48 pixels
    private final int maxScreenCol = 16; // 16 tiles wide
    private final int maxScreenRow = 12; // 12 tiles tall
    private final int screenWidth = tileSize * maxScreenCol; // 768 pixels wide
    private final int screenHeight = tileSize * maxScreenRow; // 576 pixels tall
    private BufferedImage farmerImage;
    private BufferedImage crowImage;
    private BufferedImage cornSeedImage;
    private BufferedImage cornGrowingImage;
    private BufferedImage cornMatureImage;
    private BufferedImage cornWitheredImage;
    private BufferedImage scarecrowImage;
    private BufferedImage sellingCenterImage;
    private BufferedImage seedConversionCenterImage;
    private BufferedImage backgroundImage;

    private final GameEngine gameEngine;

    // CONSTRUCTOR
    public GamePanel(GameEngine gameEngine) {

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.gameEngine = gameEngine;
        this.setLayout(new BorderLayout());
        this.setDoubleBuffered(true);

        try {
            backgroundImage = ImageIO.read(new File("images/map.png"));
            farmerImage = ImageIO.read(new File("images/farmer.png").getAbsoluteFile());
            crowImage = ImageIO.read(new File("images/crow.png").getAbsoluteFile());
            cornSeedImage = ImageIO.read(new File("images/cornSeed.png").getAbsoluteFile());
            cornGrowingImage = ImageIO.read(new File("images/cornGrowing.png").getAbsoluteFile());
            cornMatureImage = ImageIO.read(new File("images/cornMature.png").getAbsoluteFile());
            cornWitheredImage = ImageIO.read(new File("images/cornWithered.png").getAbsoluteFile());
            scarecrowImage = ImageIO.read(new File("images/scarecrow.png").getAbsoluteFile());
            sellingCenterImage = ImageIO.read(new File("images/cornmarket.png").getAbsoluteFile());
            seedConversionCenterImage = ImageIO.read(new File("images/cornseedcenter.png").getAbsoluteFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = Toolkit.getDefaultToolkit().getImage(imagePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    // DRAW
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        Graphics2D g2d = (Graphics2D) g;

        //for (Units unit : gameEngine.getUnits()) { /// ISSUE : THREAD SAFETY
        for (int i = 0; i < gameEngine.getUnits().size(); i++) {
            Units unit = gameEngine.getUnits().get(i);
            BufferedImage currentImage = null;
            Point position = unit.getPosition();
            // Draw the farmer
            switch (unit) {
                case Farmer farmer -> {
                    currentImage = farmerImage;
                    FarmerMouseListener farmerMouseListener = new FarmerMouseListener(gameEngine);
                    this.addMouseListener(farmerMouseListener);
                    g.setColor(farmer.isSelected() ? Color.BLUE : Color.RED);
                    g.fillOval(position.x - 48, position.y - 48, tileSize * 2, tileSize * 2);
                    g.setColor(Color.BLACK);
                    g.drawOval(position.x - farmer.getScareRange() / 2, position.y - farmer.getScareRange() / 2, farmer.getScareRange(), farmer.getScareRange()); // represent the farmer as a circle

                    // Draw a line from the farmer to the destination
                    if (farmer.getDestination() != null) {
                        g2d.setColor(Color.BLACK);
                        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{5.0f}, 0.0f));
                        g2d.drawLine(position.x, position.y, ((Farmer) unit).getDestination().x, ((Farmer) unit).getDestination().y);
                    }
                    g2d.drawImage(currentImage, position.x - 48, position.y - 48, tileSize * 2, tileSize * 2, null);
                }
                // Draw the crow
                case Crow crow -> {
                    currentImage = crowImage;
                    g2d.drawImage(currentImage, position.x - 24, position.y - 24, tileSize, tileSize, null);
                    // Draw a circle representing the safety distance of the crow centered at the crow's position
                    g2d.setColor(Color.BLACK);
                    g2d.drawOval(position.x - crow.getSafetyDistance() / 2, position.y - crow.getSafetyDistance() / 2, crow.getSafetyDistance(), crow.getSafetyDistance());
                }
                // Draw the corn
                case Corn corn -> {
                    currentImage = switch (corn.getLifeCycle()) {
                        case SEED -> cornSeedImage;
                        case GROWING -> cornGrowingImage;
                        case MATURE -> cornMatureImage;
                        case WITHERED -> cornWitheredImage;
                    };
                    g2d.setColor(corn.getCurrentColor());
                    g2d.fillOval(position.x - 24, position.y - 24, 48, 48);
                    g2d.drawImage(currentImage, position.x - 24, position.y - 24, tileSize, tileSize, null);
                }
                // Draw the scarecrow
                case Scarecrow scarecrow -> {
                    currentImage = scarecrowImage;
                    if (scarecrow.isTaken()) {
                        g2d.drawImage(currentImage, position.x - 36 - tileSize / 2, position.y - 36 - tileSize / 2, (int) (tileSize * 1.5), (int) (tileSize * 1.5), null);
                    } else {
                        g2d.drawImage(currentImage, position.x - 48, position.y - 48, tileSize * 2, tileSize * 2, null);
                        // Draw a circle representing the safety distance of the scarecrow centered at the scarecrow's position
                        g.setColor(Color.BLACK);
                        g.drawOval(position.x - ((Scarecrow) unit).getEfficiencyRange() / 2, position.y - ((Scarecrow) unit).getEfficiencyRange() / 2, ((Scarecrow) unit).getEfficiencyRange(), ((Scarecrow) unit).getEfficiencyRange());
                    }
                }
                // Draw the market
                case SellingCenter sellingCenter -> {
                    currentImage = sellingCenterImage;
                    g.drawImage(currentImage, position.x - 48, position.y - 48, tileSize * 2, tileSize * 2, null);
                }
                // Draw the seed conversion center
                case SeedConversionCenter seedConversionCenter -> {
                    currentImage = seedConversionCenterImage;
                    g.drawImage(currentImage, position.x - 48, position.y - 48, tileSize * 2, tileSize * 2, null);
                }
                default -> {
                }
            }
        }
        g2d.dispose();
    }
}