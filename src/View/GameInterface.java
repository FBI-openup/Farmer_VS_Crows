package View;

import Model.Units.Corns.CornGenerationThread;
import Model.Units.Crows.CrowMovementThread;
import Controller.DisplaysThread;
import Model.Centers.SellingCenter;
import Model.GameEngine;

import javax.swing.*;
import java.awt.*;

import Model.Units.Crows.CrowGenerationThread;
import Model.Units.Famers.FarmerHealthThread;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;



public class GameInterface extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final GameEngine gameEngine;
    private final GamePanel gamePanel;
    private Timer repaintTimer;

    private JLabel countdownLabel;
    private int timeLeft = 900;

    private final JLabel healthLabel = new JLabel("Health: ");
    private final JButton scoreButton = new JButton("Score: ");
    private final JLabel seedsLabel = new JLabel("Seeds: ");
    private final JLabel cornsLabel = new JLabel("Corns: ");

    private final JButton plantCornButton = new JButton("Plant Corn");
    private final JButton dropCornButton = new JButton("Drop Corn");
    private final JButton harvestCornButton = new JButton("Harvest Corn");
    private final JButton placeCornInBagButton = new JButton("Place Corn in the bag");
    private final JButton eatCornButton = new JButton("Eat Corn");
    private final JButton takeScarecrowButton = new JButton("Take Scarecrow");
    private final JButton placeScarecrowButton = new JButton("Place Scarecrow");

    private Clip clip1;
    private Clip clip2;

    /*private final Timer updateTimer;*/
    private Timer timer;
    private final java.util.List<Thread> gameThreads = new ArrayList<>();

    public GameInterface() {
        setTitle("Farmer VS Crows");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(1000, 600);

        gameEngine = new GameEngine();

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel startPanel = createStartPanel();
        cardPanel.add(startPanel, "Start");

        gamePanel = new GamePanel(gameEngine);
        JPanel gamePanelContainer = new JPanel(new BorderLayout());
        gamePanelContainer.add(gamePanel, BorderLayout.CENTER);
        gamePanelContainer.add(createControlPanel(), BorderLayout.EAST);

        cardPanel.add(gamePanelContainer, "Game");

        JPanel victoryPanel = createVictoryPanel();
        cardPanel.add(victoryPanel, "Victory");

        JPanel gameOverPanel = createGameOverPanel();
        cardPanel.add(gameOverPanel, "GameOver");

        add(cardPanel, BorderLayout.CENTER);

        healthLabel.setFont(new Font("Arial", Font.BOLD, 20));

        scoreButton.setFont(new Font("Arial", Font.BOLD, 20));

        seedsLabel.setFont(new Font("Arial", Font.BOLD, 20));

        cornsLabel.setFont(new Font("Arial", Font.BOLD, 20));

        scoreButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Call the shop() method when the scoreLabel is clicked
                shop();
            }
        });

        setupRepaintTimer(30);

        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void backgroundmusic() {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("sounds/1.wav").getAbsoluteFile());
            clip1 = AudioSystem.getClip();
            clip1.open(audioIn);
            FloatControl gainControl = (FloatControl) clip1.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-20.0f);
            clip1.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void backgroundmusic2() {
        try {
            stopCurrentMusic(clip1);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("sounds/background.wav").getAbsoluteFile());
            clip2 = AudioSystem.getClip();
            clip2.open(audioIn);
            FloatControl gainControl = (FloatControl) clip2.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-20.0f);
            clip2.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void stopCurrentMusic(Clip clip) {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }


    private JPanel createStartPanel() {
        BackgroundPanel startPanel = new BackgroundPanel("images/background.jpg");
        startPanel.setLayout(new GridBagLayout());
        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        startButton.setPreferredSize(new Dimension(200, 80));
        startButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "Game");
            System.out.println("Game started HAHAHAHAHA");
            //stopAndClearGameThreads();
            gameEngine.initializeGameUnits();
            startGameThreads();
            //resetGameState();
            backgroundmusic2();
            gamePanel.repaint();
            timer = new Timer(1000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (timeLeft > 0) {
                        timeLeft--;
                        countdownLabel.setText("Remaining: " + timeLeft + "s");
                    } else {
                        ((Timer)e.getSource()).stop();
                    }
                }
            });
            timer.start();

        });
        startPanel.add(startButton);
        backgroundmusic();
        return startPanel;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setPreferredSize(new Dimension(200, getHeight()));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // for padding

        // Show health of the farmer
        controlPanel.add(healthLabel, gbc);

        countdownLabel = new JLabel("Remaining: 900s");
        countdownLabel.setFont(new Font("Arial", Font.BOLD, 17));
        controlPanel.add(countdownLabel, gbc);


        // Add buttons
        plantCornButton.addActionListener(e -> {
            gameEngine.getFarmer().plantSeed();
            //updateSeedDisplay();
        });
        dropCornButton.addActionListener(e -> {
            gameEngine.getFarmer().dropCornAtHarvestingCenter();
        });
        harvestCornButton.addActionListener(e -> {
            gameEngine.getFarmer().collectCorn();
        });
        placeCornInBagButton.addActionListener(e -> {
            gameEngine.getFarmer().placeCornInBag();
        });
        eatCornButton.addActionListener(e -> {
            gameEngine.getFarmer().eatCorn();
        });
        takeScarecrowButton.addActionListener(e -> {
            gameEngine.getFarmer().takeScarecrow();
        });
        placeScarecrowButton.addActionListener(e -> {
            gameEngine.getFarmer().placeScareCrow();
        });
        for (JButton button : new JButton[]{plantCornButton, dropCornButton, harvestCornButton, placeCornInBagButton, eatCornButton, takeScarecrowButton, placeScarecrowButton}) {
            controlPanel.add(button, gbc);
        }

        // Add score display
        controlPanel.add(scoreButton, gbc);

        // Add seeds and corns display
        controlPanel.add(seedsLabel, gbc);
        controlPanel.add(cornsLabel, gbc);


        return controlPanel;
    }

    // Create the victory panel
    private JPanel createVictoryPanel() {
        BackgroundPanel victoryPanel = new BackgroundPanel("images/vic.jpg");
        victoryPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Add some vertical space at the top
        gbc.gridy = 1;
        victoryPanel.add(Box.createVerticalStrut(200), gbc);

        JButton returnButton = new JButton("Restart");
        returnButton.setPreferredSize(new Dimension(200, 50));
        returnButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "Start");
            gamePanel.repaint();
        });
        gbc.gridy = 2; // Set gridy to 2 for the restart button
        victoryPanel.add(returnButton, gbc);

        // Display the time taken to win the game
        JLabel timeTakenLabel = new JLabel("Time taken: " + (900 - timeLeft) + "s");
        timeTakenLabel.setFont(new Font("Arial", Font.BOLD, 54));
        timeTakenLabel.setForeground(Color.BLACK);
        gbc.gridy = 3; // Set gridy to 3 for the time taken label
        victoryPanel.add(timeTakenLabel, gbc);

        return victoryPanel;
    }

    // Create the game over panel
    private JPanel createGameOverPanel() {
        BackgroundPanel gameOverPanel = new BackgroundPanel("images/def.png");
        gameOverPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Add some vertical space at the top
        gbc.gridy = 1;
        gameOverPanel.add(Box.createVerticalStrut(200), gbc);

        JButton returnButton = new JButton("Restart");
        returnButton.setPreferredSize(new Dimension(200, 50));
        returnButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "Start");
            gamePanel.repaint();
        });
        gbc.gridy = 2; // Set gridy to 2 for the restart button
        gameOverPanel.add(returnButton, gbc);

        JLabel scoreLabel = new JLabel("Final score : " + gameEngine.getScore());
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 54));
        scoreLabel.setForeground(Color.BLACK);
        gbc.gridy = 3; // Set gridy to 3 for the score label
        gameOverPanel.add(scoreLabel, gbc);

        return gameOverPanel;
    }

    // Stop the game
    public void stopGame() {
        stopSound();
        stopAndClearGameThreads();
        gameEngine.resetGameState();
        resetGameCountdown();
        cardLayout.show(cardPanel, "Start");
    }

    // Stop the sound
    public void stopSound() {
        clip1.stop();
        clip2.stop();
    }

    // Reset the game countdown timer
    private void resetGameCountdown() {
        timeLeft = 900;
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        countdownLabel.setText("Remaining: " + timeLeft + "s");
    }

    public void updateDisplays() {
        int health = gameEngine.getFarmer().getHealth();
        healthLabel.setText("Health: " + health + "/100");
        if (health <= 30) {
            healthLabel.setForeground(new Color(255, 0, 0)); // RGB for red
        } else if (health <= 50) {
            healthLabel.setForeground(new Color(200, 100, 80)); // RGB for orange
        } else if (health <= 70) {
            healthLabel.setForeground(new Color(255, 165, 3)); // RGB for yellow
        } else if (health <= 90) {
            healthLabel.setForeground(new Color(0, 128, 0)); // RGB for green
        } else {
            healthLabel.setForeground(new Color(0, 0, 255)); // RGB for blue
        }
        if (health <= 0) {
            SwingUtilities.invokeLater(() -> {
                stopGame();
                timer.stop();
                cardLayout.show(cardPanel, "GameOver");
                gamePanel.repaint();
            });
        }

        healthLabel.setText("Health: " + gameEngine.getFarmer().getHealth() + "/100");

        scoreButton.setText("Money: " + gameEngine.getScore());
        if (gameEngine.getScore() >= 50) {
            SwingUtilities.invokeLater(() -> {
                stopGame();
                timer.stop();
                cardLayout.show(cardPanel, "Victory");
                gamePanel.repaint();
            });
        }

        //seedsLabel.setText("Seeds: " + gameEngine.getSeedConversionCenter().getNumSeeds()+" "+gameEngine.getSeedConversionCenter().getUpdateSeed());
        seedsLabel.setText("Seeds: " + gameEngine.getSeedConversionCenter().getNumSeeds());

        cornsLabel.setText("Corns: " + gameEngine.getFarmer().getNumCornForFood());

        plantCornButton.setEnabled(gameEngine.getFarmer().plantSeedClickable());
        dropCornButton.setEnabled(gameEngine.getFarmer().dropCornClickable());
        harvestCornButton.setEnabled(gameEngine.getFarmer().harvestCornClickable());
        placeCornInBagButton.setEnabled(gameEngine.getFarmer().placeCornInBagClickable());
        eatCornButton.setEnabled(gameEngine.getFarmer().eatCornClickable());
        takeScarecrowButton.setEnabled(gameEngine.getFarmer().takeScarecrowClickable());
        placeScarecrowButton.setEnabled(gameEngine.getFarmer().placeScarecrowClickable());

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

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    private void setupRepaintTimer(int delay) {
        repaintTimer = new Timer(delay, e -> {
            gamePanel.revalidate();
            gamePanel.repaint();
        });
        repaintTimer.start();
    }

    private void startGameThreads() {

        DisplaysThread displaysThread = new DisplaysThread(this);
        CrowMovementThread crowMovementThread = new CrowMovementThread(gameEngine);
        CrowGenerationThread crowGenerationThread = new CrowGenerationThread(gameEngine);
        CornGenerationThread cornGenerationThread = new CornGenerationThread(gameEngine);
        FarmerHealthThread farmerHealthThread = new FarmerHealthThread(gameEngine);
        gameEngine.getFarmer().setTimers();

        gameThreads.addAll(Arrays.asList(displaysThread, crowMovementThread, crowGenerationThread, cornGenerationThread, farmerHealthThread));
        gameThreads.forEach(Thread::start);
    }

    private void stopAndClearGameThreads() {
        if (gameThreads != null) {
            for (Thread thread : gameThreads) {
                thread.interrupt();
            }
            gameThreads.clear();
        }
        gameEngine.getFarmer().stopTimers();
    }

    private void shop() {
        SellingCenter sellingCenter = gameEngine.getSellingCenter();
        if (sellingCenter != null) {
            // Create a panel with two buttons
            JPanel panel = new JPanel();
            JButton buyScarecrowButton = new JButton("Buy Scarecrow: $10");
            JButton buySeedButton = new JButton("Buy Seed $2");
            panel.add(buyScarecrowButton);
            panel.add(buySeedButton);

            // Add action listeners to the buttons
            buyScarecrowButton.addActionListener(e -> {
                boolean boughtScarecrow = sellingCenter.buyScarecrow();
                if (boughtScarecrow) {
                    System.out.println("Scarecrow bought successfully");
                    gameEngine.createScarecrowNearPlayer();
                } else {
                    System.out.println("Need more money to buy scarecrow");
                }
            });

            buySeedButton.addActionListener(e -> {
                boolean boughtSeed = sellingCenter.buySeed();
                if (boughtSeed) {
                    System.out.println("Seed bought successfully");
                    gameEngine.getSeedConversionCenter().increaseSeeds();
                } else {
                    System.out.println("Need more money to buy seed");
                }
            });

            // Create a dialog with the panel
            JDialog dialog = new JDialog(this, "Shop", true);
            dialog.setContentPane(panel);
            dialog.setSize(new Dimension(200, 100));
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        }
    }
}