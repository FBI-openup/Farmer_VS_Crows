package Model.Units;

import Model.GameEngine;

import java.awt.Point;

/*
    * Units class
    * This class is responsible for managing the units in the game
*/
public class Units {

    // Properties
    protected Point position;
    protected GameEngine gameEngine;

    // Constructor
    public Units(Point position, GameEngine gameEngine) {
        this.position = position;
        this.gameEngine = gameEngine;
    }

    // Get the position of the unit
    public Point getPosition() {
        return position;
    }

    // Set the position of the unit
    public void setPosition(Point position) {
        this.position = position;
    }

}