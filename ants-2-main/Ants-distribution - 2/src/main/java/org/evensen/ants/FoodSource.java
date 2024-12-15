package org.evensen.ants;

import java.util.Random;

public class FoodSource {

    final int foodCount = 1000;

    private int width;
    private int height;

    public int foodLeft;
    public Position p;
    public int radius;

    public FoodSource(int width, int height){
        Random rand = new Random();
        this.foodLeft = this.foodCount;
        this.p = new Position(rand.nextFloat(0, width), rand.nextFloat(0, height));
        this.radius = 10;

        this.width = width;
        this.height = height;
    }



    public boolean containsFood(){
        return 0 < this.foodLeft;
    }

    public void takeFood(){
        this.foodLeft--;
        /*
        if(containsFood()){
            this.foodLeft--;
        }
        else {
            Random rand = new Random();
            this.foodLeft = this.foodCount;
            this.p = new Position(rand.nextFloat(0, this.width), rand.nextFloat(0, this.height));
        }
         */
    }

    public void resetFoodSource(){
        Random rand = new Random();
        this.foodLeft = this.foodCount;
        this.p = new Position(rand.nextFloat(this.radius, this.width - this.radius), rand.nextFloat(this.radius, this.height - this.radius));
    }



}


/*
Ett problem som kan inträffa är att mat kan ta slut. I stället för att bara låta mat vara oändligt tillgänglig och i en fast position, låt varje matkälla kunna ta slut.
Här är det lämpligt att skapa en ny klass, FoodSource, som ska ha en position, radie (kan vara samma för alla objekt av FoodSource-typ) och ett visst antal myrmunsbitar.
Tips på metoder för FoodSource: containsFood() och takeFood(). Om containsFood() är falsk så ska mängden mat inte minskas. MyAntWorld bör när en matkälla tagit slut ta bort den och skapa en ny matkälla på slumpmässigt ställe i myrvärlden.
Metoderna pickupFood och containsFood kommer att påverkas.
 */