package battleship;

import java.io.IOException;
import java.util.Scanner;

public class StringReader extends Thread {

    @Override
    public void run() {
        try {
            System.in.reset();
        } catch (IOException e) {
            System.out.println("System.in reset");
        }
        Scanner scanner = new Scanner(System.in);
        ClientModel.setTempString(scanner.nextLine());
    }
}