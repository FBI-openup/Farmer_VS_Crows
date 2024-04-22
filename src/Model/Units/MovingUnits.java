package Model.Units;

import Model.GameEngine;

import java.awt.Point;

/*
    * MovingUnits class
    * This class is responsible for managing the moving units in the game
*/
public abstract class MovingUnits extends Units {

    // Properties
    protected double speed;
    protected Point destination;

    // Constructor
    public MovingUnits(Point position, GameEngine gameEngine) {
        super(position, gameEngine);
        this.speed = 0;
        this.destination = position;
    }

    // Get the destination of the moving unit
    public Point getDestination() {
        return destination;
    }

    // Set the destination of the moving unit
    public void setDestination(Point destination) {
        this.destination = destination;
    }

    // Move the unit
    public abstract void move();

}