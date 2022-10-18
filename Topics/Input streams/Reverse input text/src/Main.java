import java.io.BufferedReader;
import java.io.InputStreamReader;

class Main {
    public static void main(String[] args) throws Exception {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // start coding here

        int charAsNumber = reader.read();
        String inverseInput = "";


        while (charAsNumber != -1) {

            char character =  (char) charAsNumber;
            inverseInput = character + inverseInput;

            charAsNumber = reader.read();

        }

        reader.close();

        System.out.println(inverseInput);
    }
}