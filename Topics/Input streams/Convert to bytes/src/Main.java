import java.io.InputStream;

class Main {
    public static void main(String[] args) throws Exception {
        InputStream inputStream = System.in;
        byte[] bytes = inputStream.readAllBytes();

        for (int i = 0; i < bytes.length; i++) {
            System.out.print(bytes[i]);
        }

    }
}