package battleship;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Game {

    boolean status;
    boolean isPlayerOneTurn = true;
    Player player1;
    Player player2;

    Game () {

        Scanner scanner = new Scanner(System.in);
        String input = "";
        Player player1 = new Player("player1");
        Player player2 = new Player("player2");

        System.out.println("Player 1, place your ships on the game field");
        System.out.println("");
        player1.myField.paintGameField(false);
        player1.myField.positionShips();

        System.out.println("Press Enter and pass the move to another player");
        input = scanner.nextLine();

        System.out.println("Player 2, place your ships on the game field");
        System.out.println();
        player2.myField.paintGameField(false);
        player2.myField.positionShips();

        System.out.println("Press Enter and pass the move to another player");
        input = scanner.nextLine();


        //player1.myField.paintGameField(true);
        //System.out.println("--------------------");
        //player1.myField.paintGameField(false);


        while (player1.myField.activeShips > 0 && player2.myField.activeShips > 0 ) {

            if (isPlayerOneTurn) {

                player2.myField.paintGameField(true);
                System.out.println("--------------------");
                player1.myField.paintGameField(false);
                System.out.println(" ");

                System.out.println("Player 1, it's your turn:");

                shoot(player2.myField);

            } else {
                player1.myField.paintGameField(true);
                System.out.println("--------------------");
                player2.myField.paintGameField(false);
                System.out.println(" ");

                System.out.println("Player 2, it's your turn:");
                shoot(player1.myField);
            }

            isPlayerOneTurn = !isPlayerOneTurn;

            //shoot(whoseTurnIsIt().myField);

        }


    }

    private Player whoseTurnIsIt() {

        // In this case we are going to return the contrary player to use the contrary game field to shoot
        // This can be improved to more clarity
        if (isPlayerOneTurn) {
            return player2;
        } else {
            return player1;
        }
    }

    public void shoot(Player.GameField gameField) {

        boolean notGoodInputCoordinate = true;
        Scanner scanner = new Scanner(System.in);
        String inputCoordinate = "";


        while (notGoodInputCoordinate) {


            inputCoordinate = scanner.nextLine();

            if (gameField.isValidCoordinate(inputCoordinate)) {

                Player.GameField.Coordinate shootCoordinate = gameField.coordinates.get(inputCoordinate);

                if (shootCoordinate.isEmpty()) {
                    shootCoordinate.setValue('M');
                    shootCoordinate.setHit(true);
                    gameField.paintGameField(true);

                    System.out.println("You missed!");
                    System.out.println("Press Enter and pass the move to another player");
                    String input = scanner.nextLine();



                } else {

                    shootCoordinate.setValue('X');
                    shootCoordinate.setHit(true);

                    gameField.paintGameField(true);

                    // Here update the ship power and check if the ship is sunken
                    String key = shootCoordinate.getShipName();
                    gameField.ships.put(key,gameField.ships.get(key) -1);
                    int shipPower = gameField.ships.get(key);

                    if (shipPower==0) {
                        gameField.activeShips--;
                        if (gameField.activeShips == 0) {
                            System.out.println("You sank the last ship! You won. Congratulations!");
                        } else {
                            System.out.println("You sank a ship! Specify a new target:");
                            System.out.println("Press Enter and pass the move to another player");
                            String input = scanner.nextLine();
                        }


                        System.out.println("");
                    } else {
                        System.out.println("You hit a ship!");
                        System.out.println("Press Enter and pass the move to another player");
                        String input = scanner.nextLine();

                    }

                   // gameField.paintGameField(true);


                }

                notGoodInputCoordinate = false;

            } else {

                System.out.println("Error: You entered the wrong coordinates! Try again:");

            }

        }

    }
    private class Player {
        // This class has several attributes like: the game field which is one array 10 x 10

        String name;
        GameField myField;
        public Player (String name) {
            this.name = name;
            this.myField = new GameField();

        }

        private class GameField {

            //A Game field is composed of 100 coordinates arranged in a 10 x 10 grid
            HashMap<String, Coordinate>  coordinates;

            // A Game field has ships, and they have power indicators when power = 0 they are sunk
            HashMap<String, Integer> ships;

            int activeShips;


            GameField() {

                this.coordinates = new HashMap<>();
                this.ships = new HashMap<>();

                //Initialize the game field with all the 100 coordinates inside the hash map.

                for (int i = 1; i <= 10; i++) {
                    for (int j = 1; j <= 10; j++) {
                        String letter = String.valueOf((char) (i + 64) + String.valueOf(j));
                        coordinates.put(letter, new Coordinate(letter));
                    }
                }

                // fill the ship array
                for ( Ship ship: EnumSet.allOf(Ship.class)) {
                    ships.put(ship.getName(), ship.getLength());
                }

                this.activeShips = ships.size();
            }

            // At the beginning of the game, the player has to position the ships inside the game field
            void positionShips() {

                Scanner scanner = new Scanner(System.in);

                for (Ship s: Ship.values()) {
                    System.out.println("Enter the coordinates of the " + s.getName() + " (" + s.getLength() + " cells):");
                    System.out.println(" ");

                    boolean validInputs = false;

                    while (!validInputs) {

                        String input = scanner.nextLine();
                        String[] userCoordinates = input.split(" ");
                        //Arrays.sort(userCoordinates);

                        // Validation lists:
                        // 1. each coordinate is a valid coordinate name (ie are contained in the coordinates hashmap)
                        // 2. together as a SET of coordinates are horizontally or vertically ordered
                        // 4. The length of the SET should be the same length of the ship to be positioned.
                        // 3. In order to position a ship, the set should be empty.
                        //    NOTE it must be separated by at least one coordinate from other vessels. //IMPROVEMENT//

                        // 1. Here we validate that the user input are valid coordinates. we validate two inputs only.
                        if ( this.coordinates.containsKey(userCoordinates[0]) && this.coordinates.containsKey(userCoordinates[1]) ) {

                            // 2. Check if it is a valid SET
                            if ( isValidSet(userCoordinates[0],userCoordinates[1]) ) {

                                // 3. Check if it fits
                                int length = getSetLength(getSet(userCoordinates[0],userCoordinates[1]));

                                if (length == s.getLength()) {

                                    // 4. Check every coordinate and make sure is empty of vessels.
                                    if (isSetEmpty(getSet(userCoordinates[0],userCoordinates[1]))) {

                                        // 5. Check the extended set to be sure it is not positioned to close to another vessel
                                        if (isSetEmpty(getExtendedSet(getSet(userCoordinates[0],userCoordinates[1])))){

                                            // After all validations made, here we are going to put our ship:
                                            fillSet(getSet(userCoordinates[0],userCoordinates[1]), s);

                                            // Paint the game field after putting our ship
                                            this.paintGameField(false);

                                            validInputs = true;
                                        } else {
                                            System.out.println("Error! You placed it too close to another one. Try again:");
                                        }

                                    } else {
                                        System.out.println("Error: coordinates already used! Try Again:");
                                    }
                                } else {
                                    System.out.println("Error: Wrong length of the " +  s.getName() + "! Try again:" );
                                }
                            } else {
                                System.out.println("Error: Wrong Ship Location! Try again:");
                            }
                        } else {
                            System.out.println("Error: Bad coordinates! Try again:");
                        }

                    }

                }
            }

            private void fillSet(String[] set, Ship s) {
                //Fill the coordinates in the set with the ship name

                for ( String str: set) {

                    this.coordinates.get(str).setValue('O');
                    this.coordinates.get(str).setShipName(s.getName());
                    this.coordinates.get(str).setEmpty(false);

                }

            }

            private String[] getExtendedSet(String[] set) {

                HashSet<String> extendedSet = new HashSet<>();

                for (int i = 0; i < set.length; i++) {

                    // Split the coordinate
                    String param01 = set[i].substring(0,1);
                    int param02 = Integer.parseInt(set[i].substring(1));  // here because the number can be 1 or two spaces long

                    // lots of IF // it can be done better

                    // we need to create a set with all the adjacent coordinates
                    // they can be, in the highest cases 8, middle cases 5 and, in the lowest cases 3 (the corners)
                    //TOP-LEFT, TOP, TOP-RIGHT = 3
                    //MID-LEFT, MID-RIGHT = 2
                    //BOTTOM-LEFT, BOTTOM, BOTTOM RIGHT = 3

                    //Coordinates having letter A like A1, A2... A10 do not have TOPS
                    //Coordinates having letter J like J1, J2... J10 do not have BOTTOMS
                    //Coordinates having number 1 like A1, B1 ...J1 do not have LEFTS
                    //Coordinates having number 10 like A10, B10...J10 do not have RIGHTS

                    // Addressing the most common of the coordinates, the one in the center
                    if (param01.matches("[B-I]") && (param02 >= 2 && param02 <= 9 )){

                        //add the 3 top coordinates
                        char topRow = (char)(param01.charAt(0) - 1);
                        extendedSet.add(String.valueOf((char)(topRow) + String.valueOf(param02 - 1)));
                        extendedSet.add(String.valueOf((char)(topRow) + String.valueOf(param02 )));
                        extendedSet.add(String.valueOf((char)(topRow) + String.valueOf(param02 - + 1)));

                        //add the 2 middle coordinates
                        extendedSet.add(param01 + String.valueOf(param02 - 1 ));
                        extendedSet.add(param01 + String.valueOf(param02 + 1 ));

                        //add the 3 bottom coordinates
                        char bottomRow = (char)(param01.charAt(0) + 1);
                        extendedSet.add(String.valueOf((char)(bottomRow) + String.valueOf(param02 - 1)));
                        extendedSet.add(String.valueOf((char)(bottomRow) + String.valueOf(param02 )));
                        extendedSet.add(String.valueOf((char)(bottomRow) + String.valueOf(param02 + 1)));

                    }

                    // Addressing the top row a2 through a9
                    if (param01.equalsIgnoreCase("A") && ( param02 >=2 && param02 <=9) ) {

                        //add the 2 middle coordinates
                        extendedSet.add(param01 + String.valueOf(param02 - 1 ));
                        extendedSet.add(param01 + String.valueOf(param02 + 1 ));

                        //add the 3 bottom coordinates
                        char bottomRow = (char)(param01.charAt(0) + 1);
                        extendedSet.add(String.valueOf((char)(bottomRow) + String.valueOf(param02 - 1)));
                        extendedSet.add(String.valueOf((char)(bottomRow) + String.valueOf(param02 )));
                        extendedSet.add(String.valueOf((char)(bottomRow) + String.valueOf(param02 + 1)));

                    }

                    // Addressing the bottom row I2 through I9
                    if (param01.equalsIgnoreCase("I") && ( param02 >=2 && param02 <=9) ) {

                        //add the 2 middle coordinates
                        extendedSet.add(param01 + String.valueOf(param02 - 1 ));
                        extendedSet.add(param01 + String.valueOf(param02 + 1 ));

                        //add the 3 bottom coordinates
                        char bottomRow = (char)(param01.charAt(0) + 1);
                        extendedSet.add(String.valueOf((char)(bottomRow) + String.valueOf(param02 - 1)));
                        extendedSet.add(String.valueOf((char)(bottomRow) + String.valueOf(param02 )));
                        extendedSet.add(String.valueOf((char)(bottomRow) + String.valueOf(param02 + 1)));

                    }

                    // Addressing A1
                    if (param01.equalsIgnoreCase("A") && param02 == 1 ) {
                        extendedSet.add("A2");
                        extendedSet.add("B2");
                        extendedSet.add("B1");
                    }

                    // Addressing A10
                    if (param01.equalsIgnoreCase("A") && param02 == 10 ) {
                        extendedSet.add("A9");
                        extendedSet.add("B10");
                        extendedSet.add("B9");
                    }

                    // Addressing J1
                    if (param01.equalsIgnoreCase("J") && param02 == 1 ) {
                        extendedSet.add("I1");
                        extendedSet.add("I2");
                        extendedSet.add("J2");
                    }

                    // Addressing J10
                    if (param01.equalsIgnoreCase("J") && param02 == 10 ) {
                        extendedSet.add("I9");
                        extendedSet.add("I10");
                        extendedSet.add("J10");
                    }


                }

                String[] arr = new String[extendedSet.size()];
                return extendedSet.toArray(arr);

            }


            //
            // before calling this method, coordinates has to be checked are a valid set (contiguous)
            private String[] getSet(String coordinate0, String coordinate1) {

                String[] arrToReturn = new String[0];

                // Return an array of strings with the name of the coordinates that make the set.
                // for an easy loop through.

                String param01 = coordinate0.substring(0,1);
                int param02 = Integer.parseInt(coordinate0.substring(1));  // here because the number can be 1 or two spaces long

                String param11 = coordinate1.substring(0,1);
                int param12 = Integer.parseInt(coordinate1.substring(1));  // here because the number can be 1 or two spaces long


                // The set is in a row having equal row letter (ie A, B...J)
                if (param01.equalsIgnoreCase(param11)) {

                    // Calculate the size of the string array to be returned;
                    // sometimes the user enters the parameters inverted ie (J10 J5)
                    // for this reason we need to check that the array size is always positive
                    // it means subtracting the lower value from the higher value

                    int arraySize = ((Math.max(param12, param02)) - (Math.min(param12, param02))) + 1;
                    int startingColumn = (Math.min(param12, param02));
                    arrToReturn = new String[arraySize];


                    for( int i = 0; i <= arraySize -1; i++) {
                        arrToReturn[i] = param01.concat(String.valueOf(startingColumn));
                        startingColumn++;
                    }


                // The set is in a column having equal column number (ie 1, 2...10)
                } else if ( param02 == param12) {

                    //Calculate the size of the string array to be returned;
                    //We need to convert to char BOTH string in order to loop through

                    // Important, since parameters can be inputted by the user in disorder ie  H8 J8 instead J8 H8
                    // We need to order the initial and ending element according the letter of the alphabet (lower to higher)

                    char init = (char) Math.min( param01.toUpperCase().charAt(0),param11.toUpperCase().charAt(0));
                    char end = (char) Math.max(param01.toUpperCase().charAt(0),param11.toUpperCase().charAt(0));


                    int arrLength = 0;
                    for (char i = init; i <= end; i++) {
                        arrLength += 1;
                    }

                    arrToReturn = new String[arrLength];

                    for (char i = 0; i <= arrLength -1; i++) {
                        arrToReturn[i] = String.valueOf((char)init).concat (String.valueOf(param02));
                        init++;
                    }

                    return arrToReturn;

                }

                return arrToReturn;

            }

            private boolean isSetEmpty(String[] set) {

                boolean mReturn = true;

                for ( String str: set) {

                    if (this.coordinates.get(str).getValue() !='~') {
                        mReturn = false;
                    }
                }



                return mReturn;
            }

            private boolean isValidCoordinate(String coordinate) {
                return this.coordinates.containsKey(coordinate);
            }

            // We need to check if a set of coordinates is valid inside a game field.
            private boolean isValidSet(String coordinate0, String coordinate1) {

                // a valid set is a contiguous horizontal or vertical space
                // not diagonal, not used by other ship.
                // first we decompose the coordinate parameters into two separate values the letter and the number
                // ie A4 will be A (the row) and 4 (the column)
                // we do this for both parameters...
                // now we need to check they are horizontally or vertically contiguous
                //When they are not contiguous?
                //when the row (letter) are different and the columns (numbers) are different
                // ie A1 to B2    C4 to F8 and so on
                // so if the row (letter) is different, the column should be the same
                // ie A5 to F5
                // and id the column is  different let say column 6 and column 9
                // the row should be the same ie C6 to C9

                String param01 = coordinate0.substring(0,1);
                int param02 = Integer.parseInt(coordinate0.substring(1,2));  // here because the number can be 1 or two spaces long

                String param11 = coordinate1.substring(0,1);
                int param12 = Integer.parseInt(coordinate1.substring(1,2));  // here because the number can be 1 or two spaces long

                //those should be legit set of coordinates
                return param01.equalsIgnoreCase(param11) || (param02 == param12);

            }

            private int getSetLength(String[] set) {

                return set.length;

            }

/*
            private void paintShoot(String coordinate) {
                //This method will paint the shot the user made

                // print a horizontal line with numbers from 1 to 10
                System.out.println("  1 2 3 4 5 6 7 8 9 10");

                for (int i = 0; i < 10; i++) {

                    // Print the first vertical letter (from A to J)
                    int letter = i + 65;
                    System.out.printf("%c", i + 65);

                    for (int j = 1; j <= 10; j++) {
                            String coordinateName = String.valueOf((char)letter) + String.valueOf(j);
                            if (coordinateName.equalsIgnoreCase(coordinate)) {
                                System.out.printf(" " + this.coordinates.get(coordinateName).getValue());
                            } else {
                                System.out.printf(" ~");

                            }

                    }

                    System.out.println("");
                }

            }
*/

            private void paintGameField(boolean fog) {
                //This method will paint the game field

                // print a horizontal line with numbers from 1 to 10
                System.out.println("  1 2 3 4 5 6 7 8 9 10");

                for (int i = 0; i < 10; i++) {

                    // Print the first vertical letter (from A to J)
                    int letter = i + 65;
                    System.out.printf("%c", i + 65);

                    for (int j = 1; j <= 10; j++) {

                        String coordinateName = String.valueOf((char)letter) + String.valueOf(j);

                        if (fog) {

                            System.out.printf( this.coordinates.get(coordinateName).getValue() =='O'? " ~" : " " + String.valueOf(this.coordinates.get(coordinateName).getValue()));

                        } else  {

                            System.out.printf(" " + this.coordinates.get(coordinateName).getValue());
                        }

                        //System.out.println(coordinateName);

                    }

                    System.out.println("");
                }

                System.out.println("");
            }




            class Coordinate {
                // A coordinate is a cell or a position in the game field grid
                // with the following attributes:
                // Name: A1, A2, B1, B2...
                // value: a character being ~ (empty)  O (used by a vessel)  X (vessel hit) M (miss: shot on empty cell)
                // isEmpty: true or false
                // ship: the name of the ship if is not empty
                // beenHit: true or false

                private String name;
                private char value;
                private boolean empty;
                private boolean hit;

                private String shipName;

                Coordinate(String name) {

                    this.setName(name);
                    this.setValue('~');
                    this.setEmpty(true);
                    this.setHit(false);

                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public char getValue() {
                    return value;
                }

                public void setValue(char value) {

                    this.value = value;
                }

                public boolean isEmpty() {
                    return empty;
                }

                public void setEmpty(boolean empty) {
                    this.empty = empty;
                }

                public boolean isHit() {
                    return hit;
                }

                public void setHit(boolean hit) {


                    this.hit = hit;
                }

                public String getShipName() {
                    return shipName;
                }

                public void setShipName(String shipName) {
                    this.shipName = shipName;
                }
            }

        }

    }

}
