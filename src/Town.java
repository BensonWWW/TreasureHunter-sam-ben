/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */
import java.util.ArrayList;
import java.util.Objects;

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private String[] treasure = {"crown", "trophy", "gem", "dust"};
    private static ArrayList<String> obtained = new ArrayList<>();
    private boolean searched = false;
    private boolean dug = false;


    public Hunter getHunter(){
        return hunter;
    }

    public boolean getSearched() { return searched; }

    public boolean getDug() { return dug;}

    public static ArrayList<String> getObtained(){
        return obtained;
    }

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */


    public Town(Shop shop, double toughness) {
        this.shop = shop;
        this.terrain = getNewTerrain();

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;
        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public String getLatestNews() {
        return printMessage;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";
        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";
            if (checkItemBreak() && !TreasureHunter.getEasyMode()) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, you lost your " + item;
            }
            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        printMessage = shop.enter(hunter, choice);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        boolean check = false;
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }
        if(TreasureHunter.getSecret()){
            int goldDiff = (int) (Math.random() * 10) + 1;
            for(String item : hunter.getKit()){
                if(item == null){
                    break;
                }
                else if (item.equals("sword")) {
                    check = true;
                    break;
                }
            }
            if(check){
                printMessage = "the brawler, seeing your sword, realizes he picked a losing fight and gives you his gold";
                printMessage += Colors.YELLOW + "\nYou received " + goldDiff + " gold." + Colors.RESET;
                hunter.changeGold(goldDiff);
            }else{
                printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n" + Colors.RESET;
                if (Math.random() > noTroubleChance) {
                    printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
                    printMessage += Colors.YELLOW + "\nYou won the brawl and receive " + goldDiff + " gold." + Colors.RESET;
                    hunter.changeGold(goldDiff);
                } else {
                    printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                    printMessage += "\nYou lost the brawl and pay " + goldDiff + " gold.";
                    hunter.changeGold(-goldDiff);
                    if(hunter.getGold() < 0){
                        printMessage += "\nYou lost all your coins\nGame Over!";
                        // End Game Code Here
                    }
                }
            }
        }
        else if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
        } else {
            printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n" + Colors.RESET;
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (Math.random() > noTroubleChance) {
                printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
                printMessage += Colors.YELLOW + "\nYou won the brawl and receive " + goldDiff + " gold." + Colors.RESET;
                hunter.changeGold(goldDiff);
            } else {
                printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                printMessage += "\nYou lost the brawl and pay " + goldDiff + " gold.";
                hunter.changeGold(-goldDiff);
                if(hunter.getGold() < 0){
                    printMessage += "\nYou lost all your coins\nGame Over!";
                    // End Game Code Here
                }
            }
        }
    }

    public String infoString() {
        return "This nice little town is surrounded by " + terrain.getTerrainName() + ".";
    }

    public void huntForTreasure(){
        double num = Math.random();
        if(num < 0.2){
            System.out.println("You found a " + treasure[0]);
            if(!obtained.contains(treasure[0])){
                obtained.add(treasure[0]);
                printMessage += "You hunted for treasure and found a " + treasure[0];
            }else {
                System.out.println("Already obtained " + treasure[0]);
                printMessage += "You hunted for treasure and found a " + treasure[0] + " but you already had a " + treasure[0];
            }
        }else if(num < 0.4){
            System.out.println("You found a " + treasure[1]);
            if(!obtained.contains(treasure[1])){
                obtained.add(treasure[1]);
                printMessage += "You hunted for treasure and found a " + treasure[1];
            }else{
                System.out.println("Already obtained " + treasure[1]);
                printMessage += "You hunted for treasure and found a " + treasure[1] + " but you already had a " + treasure[1];
            }
        }else if(num < 0.6){
            System.out.println("You found a " + treasure[2]);
            if(!obtained.contains(treasure[2])){
                obtained.add(treasure[2]);
                printMessage += "You hunted for treasure and found a " + treasure[2];
            } else{
                System.out.println("Already obtained " + treasure[2]);
                printMessage += "You hunted for treasure and found a " + treasure[2] + " but you already had a " + treasure[2];
            }
        }else{
            System.out.println("You found a " + treasure[3]);
            printMessage += "You hunted for treasure and found " + treasure[3];
        }
        searched = true;
    }

    public void digForGold() {
        if (hunter.hasItemInKit("shovel")) {
            double rand = Math.random();
            if (rand < .5) {
                System.out.println("You dug but only found dirt");
                printMessage += "You tried to dig for gold but only found dirt";
            } else if (rand > .5) {
                int randGold = (int) (Math.random() * (20) + 1);
                System.out.println("You dug up " + randGold + " gold!");
                hunter.changeGold(randGold);
                printMessage += "You tried to dig for gold and dug up " + randGold + " gold!";
            }
        } else {
            System.out.println("You can't dig for gold without a shovel!");
            printMessage += "You tried to dig for gold but you didn't have a shovel. Silly you.";
        }
        dug = true;
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random();
        if (rnd < .166) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd < .333) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd < .5) {
            return new Terrain("Plains", "Horse");
        } else if (rnd < .666) {
            return new Terrain("Desert", "Water");
        } else if (rnd < .833){
            return new Terrain("Jungle", "Machete");
        } else {
            return new Terrain("Marsh", "Boots");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        return (rand < 0.5);
    }
}