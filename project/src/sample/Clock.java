package sample;

public class Clock implements Runnable{

    private final Thread clock;
    private int time;
    private boolean isRunning;  // the thread will run while isRunning is true

    public Clock() {
        clock = new Thread(this);
        isRunning = false;
        time = 0;
    }

    public void startClock() {
        isRunning = true;
        time = 0;
        clock.start();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void stopClock() {
        isRunning = false;
    }

    // get the time of the simulation
    public int getTime() {
        return time;
    }

    @Override
    public void run() {
        while(isRunning) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            time++;
        }
    }
}

