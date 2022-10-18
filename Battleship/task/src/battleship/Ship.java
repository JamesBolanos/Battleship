package battleship;

public enum Ship {
    AIRCRAFT_CARRIER (5, "Aircraft Carrier"),
    BATTLESHIP(4, "Battleship"),
    SUBMARINE(3, "Submarine"),
    CRUISER(3, "Cruiser"),
    DESTROYER(2, "Destroyer"),
;

    private final int length;
    private final String name;
    public int getLength() {
        return this.length;
    }
    public String getName() {
        return this.name;
    }

    Ship (int length, String name) {
        this.length = length;
        this.name = name;
    }
}
