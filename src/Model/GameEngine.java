package Model;

import Model.Centers.HarvestingCenter;
import Model.Centers.SeedConversionCenter;
import Model.Centers.SellingCenter;
import Model.Units.Corns.Corn;
import Model.Units.Scarecrows.Scarecrow;
import Model.Units.Units;
import Model.Units.Crows.Crow;
import Model.Units.Famers.Farmer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/*
 * GameEngine class
 * This class is responsible for managing the game
 */
public class GameEngine {
    // Properties
    private final List<Units> units;

    // Constructor
    public GameEngine() {
        units = new ArrayList<>();
        //initializeGameUnits();
    }

    public void initializeGameUnits() {
        addUnit(new Farmer(this));
        addUnit(new Scarecrow(new Point(150, 450), this));
        addUnit(new SellingCenter(new Point(720, 150), this));
        addUnit(new SeedConversionCenter(new Point(720, 400), this));
    }

    // Add a unit to the game
    public void addUnit(Units unit) {
        units.add(unit);
    }

    // Remove a unit from the game
    public void removeUnit(Units unit) {
        units.remove(unit);
    }

    // Get all units in the game
    public List<Units> getUnits() {
        return units;
    }

    // Get the player's farmer
    public Farmer getFarmer() {
        for (Units unit : units) {
            if (unit instanceof Farmer) {
                return (Farmer) unit;
            }
        }
        return null;
    }



    // Get the crows in the game
    public List<Crow> getCrows() {
        List<Crow> crows = new ArrayList<>();
        for (Units unit : units) {
            if (unit instanceof Crow) {
                crows.add((Crow) unit);
            }
        }
        return crows;
    }

    // Get the corns in the game
    public List<Corn> getCorns() {
        List<Corn> corns = new ArrayList<>();
        for (Units unit : units) {
            if (unit instanceof Corn) {
                corns.add((Corn) unit);
            }
        }
        return corns;
    }

    // Get the scarecrows in the game
    public List<Scarecrow> getScarecrows() {
        List<Scarecrow> scarecrows = new ArrayList<>();
        for (Units unit : units) {
            if (unit instanceof Scarecrow) {
                scarecrows.add((Scarecrow) unit);
            }
        }
        return scarecrows;
    }

    public void createScarecrowNearPlayer() {
        // Get the player's position
        Point playerPosition = getFarmer().getPosition();

        // Create a new position for the scarecrow near the player
        Point scarecrowPosition = new Point(playerPosition.x + 10, playerPosition.y + 10); // Adjust these values as needed

        // Create a new Scarecrow at the new position
        Scarecrow scarecrow = new Scarecrow(scarecrowPosition, this);

        // Add the new Scarecrow to the game
        addUnit(scarecrow);
    }

    // Get the harvesting centers in the game
    public List<HarvestingCenter> getHarvestingCenters() {
        List<HarvestingCenter> harvestingCenters = new ArrayList<>();
        for (Units unit : units) {
            if (unit instanceof HarvestingCenter) {
                harvestingCenters.add((HarvestingCenter) unit);
            }
        }
        return harvestingCenters;
    }

    // Get the selling center in the game
    public SellingCenter getSellingCenter() {
        for (Units unit : units) {
            if (unit instanceof SellingCenter) {
                return (SellingCenter) unit;
            }
        }
        return null;
    }

    // Get the seed conversion center in the game
    public SeedConversionCenter getSeedConversionCenter() {
        for (Units unit : units) {
            if (unit instanceof SeedConversionCenter) {
                return (SeedConversionCenter) unit;
            }
        }
        return null;
    }

    // Generate crow
    public void generateCrow() {
        Point[] corners = new Point[]{
                new Point(0, 0),
                new Point(0, 576),
                new Point(768, 0),
                new Point(768, 576)
        };
        Point position = corners[(int) (Math.random() * 4)];
        Crow crow = new Crow(position, this);
        this.addUnit(crow);
    }


    // Generate corn
    public void generateCorn() {
        List<Corn> corns = getCorns();
        // Generate a random position for the corn
        //The corns should not be drawn too close to the edge of the game panel
        //The corns should not be drawn too close to each other
        int x = (int) (Math.random() * (768 - 200)) + 100; // x will be between 100 and 668 and 768 is the width of the game panel
        int y = (int) (Math.random() * (576 - 200)) + 100; // y will be between 100 and 476 and 576 is the height of the game panel
        if (corns.size() == 0) {
            Corn corn = new Corn(new Point(x, y), this);
            this.addUnit(corn);
        }
        for (Corn c : corns) {
            Point pos = c.getPosition();
            if (Math.abs(pos.x - x) > 16 * 3 || Math.abs(pos.y - y) > 16 * 3) {
                Corn corn = new Corn(new Point(x, y), this);
                this.addUnit(corn);
            }
        }
    }

    // Get the score (MONEY)
    public int getScore() {
        SellingCenter sellingCenter = getSellingCenter();
        if (sellingCenter != null) {
            return sellingCenter.getMoney();
        }
        return 0;
    }

    // Decrease the farmer's health
    public void decreaseFarmerHealth(int health) {
        Farmer farmer = getFarmer();
        if (farmer != null) {
            farmer.setHealth(farmer.getHealth() - health);
        }
    }

    public void resetGameState() {
        units.clear();
        //initializeGameUnits();
    }
}