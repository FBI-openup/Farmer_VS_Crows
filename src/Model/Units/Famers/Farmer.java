package Model.Units.Famers;

import Model.*;
import Model.Centers.HarvestingCenter;
import Model.Centers.SeedConversionCenter;
import Model.Centers.SellingCenter;
import Model.Units.Corns.Corn;
import Model.Units.MovingUnits;
import Model.Units.Scarecrows.Scarecrow;
import Model.Units.Corns.CornLifeCycleThread;

import java.awt.Point;
import java.util.ArrayList;
import javax.swing.Timer;

/*
    * Farmer class
 */
public class Farmer extends MovingUnits {

    // Farmer attributes
    private int health = 100; //the health of the farmer
    private boolean isSelected = false; //check if the unit is selected
    private int numCornForFood = 3; //the number of corn for food in the bag
    private final ArrayList<String> cornBag = new ArrayList<>(5); // the bag of the farmer
    private final int scareRange = 16 * 3 * 3; //the range to scare the crow
    private final int collectingDistance = 16 * 3; //the distance to collect corn
    private final double droppingDistance = 16 * 3 * 2; //the distance to drop corn
    private final Timer moveTimer = new Timer(25, e -> move()); //used to control the movement of the farmer
    private final Timer collectCornCd = new Timer(5000, e -> collectCornInCd = false);
    private boolean collectCornInCd = false;
    private Corn cornTaken = null;
    private final Timer plantSeedCd = new Timer(5000, e -> isPlantSeedInCd = false);
    private boolean isPlantSeedInCd = false;
    private final Timer takeScareCrowCd = new Timer(5000, e -> takeScareCrowInCd = false);
    private boolean takeScareCrowInCd = false;
    private Scarecrow scarecrowTaken = null;
    private final Timer placeScareCrowCd = new Timer(5000, e -> placeScareCrowInCd = false);
    private boolean placeScareCrowInCd = false;

    // Constructor
    public Farmer(GameEngine gameEngine) {
        super(new Point(384, 288), gameEngine);
        //this.setTimers();
        cornBag.add("MATURE");
        cornBag.add("MATURE");
        cornBag.add("MATURE");
    }

    // Set the timers
    public void setTimers() {
        collectCornCd.setRepeats(true);
        plantSeedCd.setRepeats(true);
        takeScareCrowCd.setRepeats(true);
        placeScareCrowCd.setRepeats(true);
        moveTimer.start();
    }

    // Stop the timers
    public void stopTimers() {
        collectCornCd.stop();
        plantSeedCd.stop();
        takeScareCrowCd.stop();
        placeScareCrowCd.stop();
        moveTimer.stop();
    }

    // Move the farmer
    @Override
    public void move() {
        //set a speed for the farmers movement
        if (health > 30 && health < 150) {
            if (scarecrowTaken != null) {
                speed = 2;
                scarecrowTaken.setPosition(position);
            } else if (cornTaken != null) {
                speed = 3;
                cornTaken.setPosition(position);
            } else {
                speed = 3;
            }
        } else {
            speed = 2;
            if (scarecrowTaken != null) {
                scarecrowTaken.setPosition(position);
            } else if (cornTaken != null) {
                cornTaken.setPosition(position);
            }
        }

        //calculate the direction to the destination
        int dx = destination.x - position.x;
        int dy = destination.y - position.y;

        // calculate the distance to the destination
        double distance = Math.sqrt(dx * dx + dy * dy);

        // if the distance is less than the speed, the farmer has reached the destination
        if (distance <= speed) {
            position.setLocation(destination);
            return;
        }

        // calculate the unit vector of the direction
        double unitX = dx / distance;
        double unitY = dy / distance;

        // calculate the movement for this frame
        int moveX = (int) (unitX * speed);
        int moveY = (int) (unitY * speed);

        // update the position each time
        position.translate(moveX, moveY);
    }

    // Collect corn
    public void collectCorn() {
        if (!collectCornInCd && cornTaken == null) {
            for (Corn corn : gameEngine.getCorns()) {
                if (position.distance(corn.getPosition()) <= collectingDistance) {
                    cornTaken = corn;
                    cornTaken.setTaken(true);
                    corn.getCornLifeCycleThread().stopThread();
                    break;
                }
            }

            // Start the Cd
            collectCornInCd = true;
            collectCornCd.start();
        }
    }

    public void dropCornAtHarvestingCenter() {
        for (HarvestingCenter center : gameEngine.getHarvestingCenters()) {
            if (position.distance(center.getPosition()) <= droppingDistance && center instanceof SellingCenter) {
                if (cornTaken.getLifeCycle() == Corn.LifeCycle.MATURE || cornTaken.getLifeCycle() == Corn.LifeCycle.GROWING)
                    center.getCorn(cornTaken);
                gameEngine.removeUnit(cornTaken);
                cornTaken = null;
                break;
            }
            else if (position.distance(center.getPosition()) <= droppingDistance && center instanceof SeedConversionCenter) {
                center.getCorn(cornTaken);
                gameEngine.removeUnit(cornTaken);
                cornTaken = null;
                break;
            }
        }
    }

    public synchronized void plantSeed() {
        if (gameEngine.getSeedConversionCenter().getNumSeeds() > 0) {
            Point pos = new Point(this.position);
            // Check if the farmer is not in Cd
            if (!isPlantSeedInCd) {
                Corn corn = new Corn(pos, gameEngine);
                gameEngine.addUnit(corn);
                CornLifeCycleThread cornLifeCycleThread = new CornLifeCycleThread(corn);
                cornLifeCycleThread.start();
                gameEngine.getSeedConversionCenter().consumeSeeds();
                isPlantSeedInCd = true;
                plantSeedCd.start();
            }
        }
    }

    // Place corn in the bag
    public void placeCornInBag() {
        if (cornTaken != null && numCornForFood < 5) {
            cornBag.add(String.valueOf(cornTaken.getLifeCycle())); // Save the state of the corn
            numCornForFood++; // Increase the number of corn in the bag
            gameEngine.removeUnit(cornTaken); // Remove the corn from the game
            cornTaken = null; // Set the corn taken by the farmer to null

        }
    }

    // Eat corn
    public void eatCorn() {
        if (cornTaken != null) {
            switch (cornTaken.getLifeCycle()) {
                case SEED:
                    health -= 15;
                    break;
                case GROWING:
                    health += 15;
                    break;
                case MATURE:
                    health += 30;
                    break;
                case WITHERED:
                    health -= 15;
                    break;
            }
            gameEngine.removeUnit(cornTaken);
            cornTaken = null;
        } else if (numCornForFood > 0) {
            switch (cornBag.get(0)) {
                case "SEED":
                    health -= 15;
                    break;
                case "MATURE":
                    health += 30;
                    break;
                case "GROWING":
                    health += 15;
                    break;
                case "WITHERED":
                    health -= 15;
                    break;
            }
            numCornForFood--;
            cornBag.remove(0);
        }
    }

    // Take scarecrow
    public void takeScarecrow() {
        if (!takeScareCrowInCd && scarecrowTaken == null) {
            for (Scarecrow scarecrow : gameEngine.getScarecrows()) {
                if (position.distance(scarecrow.getPosition()) <= collectingDistance) {
                    scarecrowTaken = scarecrow;
                    scarecrow.setTaken(true);
                    break;
                }
            }
            // Start the Cd
            takeScareCrowInCd = true;
            takeScareCrowCd.start();
        }
    }

    // Place scarecrow
    public void placeScareCrow() {
        // Check if the farmer is not in Cd
        if (!placeScareCrowInCd && scarecrowTaken != null) {
            Point pos = new Point(this.position);
            scarecrowTaken.setPosition(pos);
            scarecrowTaken.setTaken(false);
            scarecrowTaken = null;
        }
    }

    // Get the health of the farmer
    public int getHealth() {
        if (health>100)
            health = 100;
        return health;
    }

    // Set the health of the farmer
    public void setHealth(int health) {
        this.health = health;
    }

    // Get the corn taken by the farmer
    public Corn getCornTaken() {
        return cornTaken;
    }

    // Get the scarecrow taken by the farmer
    public Scarecrow getScarecrowTaken() {
        return scarecrowTaken;
    }

    // Get the selected state of the farmer
    public boolean isSelected() {
        return isSelected;
    }

    // Set the selected state of the farmer
    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    // Get the destination of the farmer
    public Point getDestination() {
        return destination;
    }

    // Set the destination of the farmer
    public void setDestination(Point destination) {
        this.destination = destination;
    }

    // Check if the click is within the circle
    public boolean isClickWithinCircle(Point clickPoint, int radius) {
        return clickPoint.distance(position) <= radius;
    }

    // Start the movement of the farmer
    public void startMoveTimer() {
        if (moveTimer != null && !moveTimer.isRunning()) {
            moveTimer.start();
        }
    }

    // Stop the movement of the farmer
    public void stopMoveTimer() {
        if (moveTimer != null && moveTimer.isRunning()) {
            moveTimer.stop();
        }
    }

    // Get the scare range of the farmer
    public int getScareRange() {
        return scareRange;
    }

    // Get the collecting distance of the farmer
    public int getCollectingDistance() {
        return collectingDistance;
    }

    // Get the number of corn for food in the bag
    public int getNumCornForFood() {
        return numCornForFood;
    }

    // Get the dropping distance of the farmer
    public double getDroppingDistance() {
        return droppingDistance;
    }

    //////////////////////////////////////////////////////////////////////////////////

    // Methods used for making the buttons unclickable or not

    // The bag is empty
    public boolean bagEmpty() {
        return numCornForFood == 0;
    }

    // The bag is full
    public boolean bagFull() {
        return numCornForFood == 5;
    }

    // Farmer has seeds
    public boolean hasSeeds() {
        return gameEngine.getSeedConversionCenter().getNumSeeds() > 0;
    }

    // Farmer is not on a corn or scarecrow or harvesting center
    public boolean notOnCornOrScarecrowOrHarvestingCenter() {
        for (Corn corn : gameEngine.getCorns()) {
            if (position.distance(corn.getPosition()) <= collectingDistance) {
                return false;
            }
        }
        for (Scarecrow scarecrow : gameEngine.getScarecrows()) {
            if (position.distance(scarecrow.getPosition()) <= collectingDistance) {
                return false;
            }
        }
        for (HarvestingCenter center : gameEngine.getHarvestingCenters()) {
            if (position.distance(center.getPosition()) <= droppingDistance) {
                return false;
            }
        }
        return true;
    }

    public boolean notOnCornOrHarvestingCenter() {
        for (Corn corn : gameEngine.getCorns()) {
            if (position.distance(corn.getPosition()) <= collectingDistance) {
                return false;
            }
        }
        for (HarvestingCenter center : gameEngine.getHarvestingCenters()) {
            if (position.distance(center.getPosition()) <= droppingDistance) {
                return false;
            }
        }
        return true;
    }

    // Farmer is holding a corn
    public boolean cornTaken() {
        return cornTaken != null;
    }

    // Farmer is holding a scarecrow
    public boolean scarecrowTaken() {
        return scarecrowTaken != null;
    }

    // Farmer is at a harvesting center
    public boolean atHarvestingCenter() {
        for (HarvestingCenter center : gameEngine.getHarvestingCenters()) {
            if (position.distance(center.getPosition()) <= droppingDistance) {
                return true;
            }
        }
        return false;
    }

    // Farmer is close to a corn
    public boolean isCloseToCorn() {
        for (Corn corn : gameEngine.getCorns()) {
            if (position.distance(corn.getPosition()) <= collectingDistance) {
                return true;
            }
        }
        return false;
    }

    // Farmer is near a seed
    public boolean isNearSeed() {
        for (Corn corn : gameEngine.getCorns()) {
            if (position.distance(corn.getPosition()) <= collectingDistance && corn.getLifeCycle() == Corn.LifeCycle.SEED) {
                return true;
            }
        }
        return false;
    }

    // Farmer is close to a scarecrow
    public boolean isCloseToScarecrow() {
        for (Scarecrow scarecrow : gameEngine.getScarecrows()) {
            if (position.distance(scarecrow.getPosition()) <= collectingDistance) {
                return true;
            }
        }
        return false;
    }

    ///////////////////////////////////////////

    // Button HarvestCorn is clickable
    public boolean harvestCornClickable() {
        return !cornTaken() && !scarecrowTaken() && isCloseToCorn() && !isNearSeed() && !collectCornInCd;
    }

    // Button DropCorn is clickable
    public boolean dropCornClickable() {
        return cornTaken() && atHarvestingCenter();
    }

    // Button PlantSeed is clickable
    public boolean plantSeedClickable() {
        return !cornTaken() && !scarecrowTaken() && hasSeeds() && !isCloseToCorn() && !isCloseToScarecrow() && !isPlantSeedInCd;
    }

    // Button EatCorn is clickable
    public boolean eatCornClickable() {
        return cornTaken() || numCornForFood > 0;
    }

    // Button PlaceCornInBag is clickable
    public boolean placeCornInBagClickable() {
        return cornTaken() && !bagFull();
    }

    // Button TakeScarecrow is clickable
    public boolean takeScarecrowClickable() {
        return !cornTaken() && !scarecrowTaken() && isCloseToScarecrow();
    }

    // Button PlaceScarecrow is clickable
    public boolean placeScarecrowClickable() {
        return scarecrowTaken() && notOnCornOrHarvestingCenter();
    }

    public void decreaseHealth() {
        health -= 1;
    }
}