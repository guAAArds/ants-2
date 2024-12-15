package org.evensen.ants;

import org.evensen.ants.Ant;
import org.evensen.ants.AntWorld;
import org.evensen.ants.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyAntWorld implements AntWorld {

    private final int width;
    private final int height;

    private float[][] foragingPheromones;
    private float[][] foodPheromones;

    private boolean[][] foodMatrix;

    private List<FoodSource> foodSources;

/*

 */

    public MyAntWorld(int width, int height, int food){
        this.width = width;
        this.height = height;

        this.foodMatrix = new boolean[width][height];

        this.foragingPheromones = new float[width][height];
        this.foodPheromones = new float[width][height];

        this.foodSources = new ArrayList<>();
        for(int i = 0; i < food; i++){
            this.foodSources.add(new FoodSource(width, height));
            updateFoodSource(foodSources.get(i), true);
        }

        this.foragingPheromones = new float[width][height];
        this.foodPheromones = new float[width][height];
    }

    private void updateFoodSource(FoodSource foodSource, boolean set) {
        Position center = foodSource.p;
        int radius = foodSource.radius;
        for (int x = (int)center.getX() - radius; x < center.getX() + radius; x++) {
            for (int y = (int)center.getY() - radius; y < center.getY() + radius; y++) {
                if (isInsideWorld(foodSource.p, foodSource) && center.isWithinRadius(new Position(x, y), radius)) {
                    this.foodMatrix[x][y] = set;
                }
            }
        }
    }

    private boolean isInsideWorld(Position p, FoodSource fs) {
        return p.getX() >= fs.radius && p.getX() < this.width - fs.radius && p.getY() >= fs.radius && p.getY() < this.height - fs.radius;
    }


    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public boolean isObstacle(final Position p) {
        final float x = p.getX();
        final float y = p.getY();

        return x < 0 || y < 0 || y >= this.height || x >= this.width;
    }

    //Kolla amount
    @Override
    public void dropForagingPheromone(final Position p, final float amount) {

        if(this.foragingPheromones[(int)p.getX()][(int)p.getY()] >= 9){
            this.foragingPheromones[(int)p.getX()][(int)p.getY()] = 10;
        }
        else {
            this.foragingPheromones[(int)p.getX()][(int)p.getY()] += 1.0f;
        }

    }

    @Override
    public void dropFoodPheromone(final Position p, final float amount) {
        if(this.foodPheromones[(int)p.getX()][(int)p.getY()] >= 9){
            this.foodPheromones[(int)p.getX()][(int)p.getY()] = 10;
        }
        else {
            this.foodPheromones[(int)p.getX()][(int)p.getY()] += 1.0f;
        }

    }

    @Override
    public void dropFood(final Position p) {

    }

    //+10?
    @Override
    public void pickUpFood(final Position p) {
        for(FoodSource fs : this.foodSources){
            if(p.isWithinRadius(fs.p, fs.radius + 10)){
                if(fs.containsFood()){
                    fs.takeFood();
                }
                else{
                    updateFoodSource(fs, false);
                    fs.resetFoodSource();
                    updateFoodSource(fs, true);
                }

            }
        }
    }

    @Override
    public float getDeadAntCount(final Position p) {
        return 0;
    }

    @Override
    public float getForagingStrength(final Position p) {
        return this.foragingPheromones[(int)p.getX()][(int)p.getY()];
    }

    @Override
    public float getFoodStrength(final Position p) {
        return this.foodPheromones[(int)p.getX()][(int)p.getY()];
    }

    @Override
    public boolean containsFood(final Position p) {
        return this.foodMatrix[(int)p.getX()][(int)p.getY()];
    }

    @Override
    public long getFoodCount() {
        return 0;
    }

    @Override
    public boolean isHome(final Position p) {
        Position home = new Position(this.width, this.height/2);
        return p.isWithinRadius(home, 20);
    }

    @Override
    public void dispersePheromones(){


    }


    //@Override
    public void selfDispersePheromones() {
        //evaporation of pheromones.
        /*
        float evaporationRate = 0.60f;
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                this.foragingPheromones[x][y] *= evaporationRate;
                this.foodPheromones[x][y] *= evaporationRate;
            }
        }

         */

        //Lets foodsorces drop food pheromones.
        for(FoodSource fs : this.foodSources){
            dropFoodPheromone(fs.p, 10);
        }
        //------------------ Disperse 2 ------------------

        //Temporära matriser som vi sedan kopierar till de riktiga
        float[][] newForagingPheromones = new float[width][height];
        float[][] newFoodPheromones = new float[width][height];

        //Konstanterna
        float k = 0.5f;
        float f = 0.80f;

        // En array med matriser (en array med foraging matrisen samt food matrisen)
        float[][][] pheromoneTypes = {foragingPheromones, foodPheromones};
        float[][][] newPheromoneTypes = {newForagingPheromones, newFoodPheromones};

        // loopar igenom alla fermontyper
        for (int t = 0; t < pheromoneTypes.length; t++) {
            float[][] pheromones = pheromoneTypes[t];
            float[][] newPheromones = newPheromoneTypes[t];

            //Loopar igenom alla punkter
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {

                    // Om positionen är ett hinder, hoppa över den
                    if (isObstacle(new Position(x, y))) {
                        newPheromones[x][y] = 0;
                        continue;
                    }

                    //Beräknar npl (neighbor pheromone level)
                    double npl = 0;
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            if (dx == 0 && dy == 0) continue; //Punkten är ej en granne till sig själv

                            int nx = x + dx;
                            int ny = y + dy;

                            //Kontroll av kanter (ska speglas ifall utanför)
                            if (nx < 0) nx = 0;
                            if (ny < 0) ny = 0;
                            if (nx >= width) nx = width - 1;
                            if (ny >= height) ny = height - 1;

                            npl += pheromones[nx][ny];
                        }
                    }

                    //beräknar ny fermon nivå
                    double currentPheromone = pheromones[x][y];
                    npl = ((1 - k) * npl) / 8 + k * currentPheromone;

                    //applicera konstanten f
                    newPheromones[x][y] = (float) (npl * f);
                }
            }
        }

        // Kopiera tillbaka värden från tmpP till de faktiska feromonmatriserna
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                foragingPheromones[x][y] = newForagingPheromones[x][y];
                foodPheromones[x][y] = newFoodPheromones[x][y];
            }
        }
    }


    @Override
    public void setObstacle(final Position p, final boolean add) {

    }

    @Override
    public void hitObstacle(final Position p, final float strength) {

    }
}