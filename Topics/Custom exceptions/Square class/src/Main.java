import java.util.Scanner;

class Square {
    int a;

    public Square(int a) throws SquareSizeException {


        if (a > 0) {

            this.a = a;
        }

        else  {

           //put you code here
            throw new SquareSizeException("zero or negative size");

        }

    }
}

class Main {
    public static void main(String[] args) throws SquareSizeException {
        Scanner scn = new Scanner(System.in);
        int a = scn.nextInt();
        //put your code here
        try {
            Square square = new Square(a);
        } catch (SquareSizeException s) {
            System.out.println(s.getMessage());

        }


    }
}

class SquareSizeException extends Exception {
    public SquareSizeException(String message) {
        super(message);
    }
}