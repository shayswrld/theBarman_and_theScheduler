package barScheduling;

import java.util.Hashtable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;


public class DrinkOrder  {

    //DO NOT change the code below
    public enum Drink { 
        Beer("Beer", 10),
        Cider("Cider", 10),
        GinAndTonic("Gin and Tonic", 30),
        Martini("Martini", 50),
        Cosmopolitan("Cosmopolitan", 80),
        BloodyMary("Bloody Mary", 90),
        Margarita("Margarita", 100),
        Mojito("Mojito", 120),
        PinaColada("Pina Colada", 200),
        LongIslandIcedTea("Long Island Iced Tea", 300),
    	B52("B52", 500);
    	
    	
        private final String name;
        private final int preparationTime; // in minutes
        private int remainingPreparationTime; //Added for round robin since prep time can't be changed


        Drink(String name, int preparationTime) {
            this.name = name;
            this.preparationTime = preparationTime;
            this.remainingPreparationTime = preparationTime;
        }

        public String getName() {
            return name;
        }

        public int getPreparationTime() {
            return preparationTime;
        }
        //Added for round robin
        public int getRemainingPreparationTime() {
            return remainingPreparationTime;
        }

        //Added for round robin
        public void setRemainingPreparationTime(int remainingPreparationTime) {
            this.remainingPreparationTime = remainingPreparationTime;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
    private final Drink drink;
    private static final Random random = new Random();
    private int orderer;
    private AtomicBoolean orderComplete;
    private long timeDone;

 //constructor
    public DrinkOrder(int patron) {
    	drink=getRandomDrink();
    	orderComplete = new AtomicBoolean(false);
    	orderer=patron;
    }
    
    public static Drink getRandomDrink() {

        Drink[] drinks = Drink.values();  // Get all enum constants
        int randomIndex = random.nextInt(drinks.length);  // Generate a random index
        return drinks[randomIndex];  // Return the randomly selected drink
    }
    

    public int getExecutionTime() {
        return drink.getPreparationTime();
    }
    //Added for round robin
    public int getRemainingExecutionTime() {
        return drink.getRemainingPreparationTime();
    }
    //Added for round robin 
    public void setExecutionTime(int executionTime) {
        drink.setRemainingPreparationTime(executionTime);
    }

    public int getOrderer() {
        // Method added to determine who ordered the drink
        return orderer;
    }

    public long getTimeDone() {
        return this.timeDone;
    }
    
    //barman signals when order is done
    public synchronized void orderDone() {
    	orderComplete.set(true);
        this.notifyAll();
        timeDone = System.currentTimeMillis();
    }
    
    //patrons wait for their orders
    //returns time taken for the order
    public synchronized void waitForOrder() throws InterruptedException {
    	while(!orderComplete.get()) {
    		this.wait();
    	}

    }
    
    @Override
    public String toString() {
        return Integer.toString(orderer) +": "+ drink.getName()+ " (" +Integer.toString(drink.getRemainingPreparationTime())+ ")";
    }
}