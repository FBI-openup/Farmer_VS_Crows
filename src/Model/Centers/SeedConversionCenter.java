package Model.Centers;

import Model.GameEngine;

import java.awt.*;

import Model.Units.Corns.Corn;

public class SeedConversionCenter extends HarvestingCenter {

    private int numSeeds = 3;

    private int updateSeed = 0;


    public SeedConversionCenter(Point position, GameEngine gameEngine) {
        super(position, gameEngine);
    }

    public int getNumSeeds() {
        return numSeeds;
    }


    public void getCorn(Corn corn) {
        // Implement the logic for converting corn to seeds
        // For example, increase the number of seeds by 2
        if (corn.getLifeCycle() == Corn.LifeCycle.MATURE) {
            numSeeds += 3;
            updateSeed = 3;
        } else if (corn.getLifeCycle() == Corn.LifeCycle.WITHERED) {
            numSeeds += 2;
            updateSeed = 2;
        } else {
            numSeeds += 1;// growing gives only 1 seed
            updateSeed = 1;
        }
    }

    /*public String getUpdateSeed() {
        String result;
        if (updateSeed == 0) {
            result = "";
        } else if (updateSeed < 0) {
            result = String.valueOf(updateSeed);
        } else {
            result = "+" + updateSeed;
        }
        updateSeed = 0; // Reset updateSeed
        return result;
    }
    */
    public void consumeSeeds() {
        // Implement the logic for getting seeds from the storage center
        // For example, decrease the number of seeds
        if (numSeeds > 0) {
            numSeeds--;
            updateSeed = -1;
        }
    }
    public void increaseSeeds() {
    numSeeds++;
    }

    /*public void resetUpdateSeed() {
        updateSeed = 0;
    }
    */

}
