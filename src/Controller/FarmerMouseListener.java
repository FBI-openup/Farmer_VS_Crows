package Controller;

import Model.*;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Point;
import javax.swing.SwingUtilities;

/*
    * This class is responsible for handling the mouse events for the farmer.
*/
public class FarmerMouseListener implements MouseListener {
    private final GameEngine gameEngine;
    private Thread movementThread; //start a new thread for the movement of the farmer
    private final int SELECTION_RADIUS = 16 * 4 ;    //the radius of the selection circle

    public FarmerMouseListener(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    // When the user clicks on the screen, the farmer will be selected or moved

    @Override
    public void mouseClicked(MouseEvent e) {
        Point clickPoint = e.getPoint(); //get the point where the user clicked
        if (SwingUtilities.isLeftMouseButton(e)) {
            // check if the user clicked within the selection circle
            if (gameEngine.getFarmer().isClickWithinCircle(clickPoint, SELECTION_RADIUS)) {
                gameEngine.getFarmer().setSelected(!gameEngine.getFarmer().isSelected()); //switch the selected state
            }
        } else if (SwingUtilities.isRightMouseButton(e) && gameEngine.getFarmer().isSelected()) {
            // when the user right click on the screen, the farmer will move to the location of the click
            Point destination = e.getPoint();
            gameEngine.getFarmer().setDestination(destination); // update the destination of the farmer
            gameEngine.getFarmer().startMoveTimer(); // make sure the farmer is moving
            if (movementThread != null && movementThread.isAlive()) {
                movementThread.interrupt(); //interrupt the previous movement
            }
            movementThread = new Thread(); // start a new thread for the movement of the farmer
            movementThread.start();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
