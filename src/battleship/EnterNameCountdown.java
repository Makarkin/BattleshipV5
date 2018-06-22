package battleship;

public class EnterNameCountdown extends Thread {

    private int counter = 10;

    @Override
    public void run() {
        while (counter > 0) {
            try {
                Thread.sleep(1000);
                counter--;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (ClientModel.getTempString() == null) {
            ClientModel.setTempString("r");
        }

        counter = 10;
    }
}