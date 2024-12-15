package org.evensen.ants;

public class DispersalPolicy1 implements DispersalPolicy {
    private final double k; // "Keep"-konstant
    private final double f; // Fall-off konstant

    public DispersalPolicy1(double k, double f) {
        this.k = k;
        this.f = f;
    }


    @Override
    public float[] getDispersedValue(AntWorld w, Position p) {

        MyAntWorld myWorld = (MyAntWorld) w;

        float foragingPheromoneLevel = 0;
        float foodPheromoneLevel = 0;

        int width = myWorld.getWidth();
        int height = myWorld.getHeight();

        if (myWorld.isObstacle(p)) {
            return new float[]{0, 0};
        }

        double foragingNPL = 0;
        double foodNPL = 0;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;

                int nx = (int) (p.getX() + dx);
                int ny = (int) (p.getY() + dy);

                if (nx < 0) nx = 0;
                if (ny < 0) ny = 0;
                if (nx >= width) nx = width - 1;
                if (ny >= height) ny = height - 1;

                Position neighbor = new Position(nx, ny);

                //Metoderna från MyAntWorld
                foragingNPL += myWorld.getForagingStrength(neighbor);
                foodNPL += myWorld.getFoodStrength(neighbor);
            }
        }


        double currentForaging = myWorld.getForagingStrength(p);
        double currentFood = myWorld.getFoodStrength(p);

        foragingNPL = ((1 - k) * foragingNPL) / 8 + k * currentForaging;
        foodNPL = ((1 - k) * foodNPL) / 8 + k * currentFood;


        foragingPheromoneLevel = (float) (foragingNPL * f);
        foodPheromoneLevel = (float) (foodNPL * f);

        return new float[]{foragingPheromoneLevel, foodPheromoneLevel};
    }
}
