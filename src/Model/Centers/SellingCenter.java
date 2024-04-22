package Model.Centers;

import Model.GameEngine;
import Model.Units.Corns.Corn;
import Model.Units.Famers.Farmer;

import java.awt.*;

public class SellingCenter extends HarvestingCenter {
    private int money = 0;
    private int numScarecrows = 3; // max number of scarecrows
    private int numSeeds = 10; // max number of seeds

    public SellingCenter(Point position, GameEngine gameEngine) {
        super(position, gameEngine);
    }

    public int getMoney() {
        return money;
    }

    @Override
    public void getCorn(Corn corn) {
        // Implement the logic for selling corn
        Farmer farmer = gameEngine.getFarmer();
        Corn cornTaken = farmer.getCornTaken();
        if (farmer.getCornTaken() != null) {
            if (cornTaken.getLifeCycle() == Corn.LifeCycle.MATURE)
                money += 3;
            else if (cornTaken.getLifeCycle() == Corn.LifeCycle.GROWING)
                money += 1;
        }
    }
    public boolean buyScarecrow() {
        // price of a scarecrow 300
        int scarecrowPrice = 10;
        if (money >= scarecrowPrice && numScarecrows > 0) {
            money -= scarecrowPrice;
            numScarecrows--;
            return true; // purchase successful
        }
        return false; // purchase failed
    }

    public boolean buySeed() {
        // price of a seed 25
        int seedPrice = 3;
        if (money >= seedPrice && numSeeds > 0) {
            money -= seedPrice;
            numSeeds--;
            return true; // purchase successful
        }
        return false; // purchase failed
    }


}
