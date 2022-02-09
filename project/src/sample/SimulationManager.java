package sample;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class SimulationManager implements Runnable {

    private final int N;  // number of clients
    private final int Q;  // number of queues
    private final int tMaxSimulation;     // time the simulation will run
    private final int tMinArrival;
    private final int tMaxArrival;
    private final int tMinService;
    private final int tMaxService;

    private boolean isRunning;
    private int time;

    private final Clock clock = new Clock();
    private final Thread simulation = new Thread(this);
    private QueueThread[] queues;

    private Controller controller = new Controller();


    private ArrayList<Client> generatedClients = new ArrayList<>();  // holds all of the initial clients that are generated randomly
    private ArrayList<Client> waitingClients = new ArrayList<>();    // holds the waiting clients during the simulation


    public SimulationManager(int n, int q, int tMaxSimulation, int tMinArrival, int tMaxArrival, int tMinService, int tMaxService) {
        N = n;
        Q = q;
        this.tMaxSimulation = tMaxSimulation;
        this.tMinArrival = tMinArrival;
        this.tMaxArrival = tMaxArrival;
        this.tMinService = tMinService;
        this.tMaxService = tMaxService;
    }

    public void start() {
        // define Q QueueThreads
        queues = new QueueThread[Q];
        generateNClients();
        for (int i = 0; i < Q; i++) {
            queues[i] = new QueueThread(i + 1, clock);
        }
        System.out.println("Starting clock");
        clock.startClock();
        isRunning = true;
        simulation.start();
    }

    // get the index of the queue that has the smallest waiting time
    private int getShortestTimeQueue() {
        int minIndex = 0;
        int minT = queues[0].getServiceTime();
        for (int i = 1; i < queues.length; i++) {
            if (queues[i].getServiceTime() < minT) {
                minT = queues[i].getServiceTime();
                minIndex = i;
            }
        }
        return minIndex;
    }

    // function that generates randomly N clients, and sorts them with respect to their arrival time
    private void generateNClients() {
        for (int i = 0; i < N; i++) {
            Random r = new Random();
            int tArrival = r.nextInt((tMaxArrival - tMinArrival) + 1) + tMinArrival;
            int tService = r.nextInt((tMaxService - tMinService) + 1) + tMinService;
            Client client = new Client(i + 1, tArrival, tService);
            generatedClients.add(client);
        }
        // sort list wrt arrivalTime
        for (int i = 0; i < N - 1; i++) {
            for (int j = i + 1; j < N; j++) {
                if (generatedClients.get(i).getArrivalTime() > generatedClients.get(j).getArrivalTime()) {
                    Client aux = generatedClients.get(i);
                    generatedClients.set(i, generatedClients.get(j));
                    generatedClients.set(j, aux);
                }
            }
        }

        copyLists();

        /*
        System.out.println("Randomly generated clients: ");
        for (int i = 0; i < N; i++) {
            System.out.println("Client " + generatedClients.get(i).getID() + ": (" + generatedClients.get(i).getID() + ", " + generatedClients.get(i).getArrivalTime() + ", " + generatedClients.get(i).getProcessingTime() + ")");
        } */
    }

    // copy the generatedClients List into the clients list
    private void copyLists() {
        for (int i = 0; i < N; i++) {
            waitingClients.add(generatedClients.get(i));
        }
    }

    // tracks the total time spent by every client in the queues and computes the average waiting time
    private double computeAverageWaitingTime() {
        int avg = 0;
        for (int i = 0; i < N; i++) {
            avg += generatedClients.get(i).getWaitingTime();
        }
        return avg / (float) N;
    }

    private double computeAverageServiceTime() {
        int avg = 0;
        for (int i = 0; i < N; i++) {
            avg += generatedClients.get(i).getServiceTime();
        }
        return avg / (float) N;
    }

    // helper function that rounds the double value to the specified decimal places
    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int getTime() {
        return time;
    }

   ArrayList<StringBuilder> out = new ArrayList<>();
    @Override
    public void run() {
        boolean waitingClientsEmpty = false;    // checks if the waiting clients list is empty
        boolean queuesEmpty = true;             // checks if all of the queues are empty
        int maxCustomers = 0;
        int peakHour = 0;

        FileWriter myWriter = null;
        try {
            myWriter = new FileWriter("output.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder output = new StringBuilder();
        // we stop the simulation when both the waiting clients list AND the queues are empty, or when the simulation time
        // is bigger than the maximum simulation time
        time = clock.getTime();

        while (clock.getTime() <= tMaxSimulation && (!waitingClientsEmpty || !queuesEmpty)) {
            // take all the clients in the waiting clients list that have the arrivalTime equal to the "current" time,
            // add them to the queues with the smallest waiting time and remove them from the waiting clients
            while (waitingClients.size() > 0 && waitingClients.get(0).getArrivalTime() == clock.getTime()) {
                int minQIndex = getShortestTimeQueue();
                queues[minQIndex].addClient(waitingClients.get(0));
                waitingClients.remove(waitingClients.get(0));
            }

            try {
                myWriter.write("Time " + clock.getTime() + System.lineSeparator());
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Time " + clock.getTime());
            output = new StringBuilder("Time " + clock.getTime() + System.lineSeparator());
            //output[time].append("Time " + clock.getTime());
            //output[time].append(System.lineSeparator());
            // print the waiting clients at each time of the simulation
            try {
                myWriter.write("Waiting clients: ");
            } catch (IOException e) {
                e.printStackTrace();
            }
            output.append("Waiting clients: ");
            System.out.print("Waiting clients: ");
            for (Client waitingClient : waitingClients) {

                try {
                    myWriter.write("(" + waitingClient.getID() + ", " + waitingClient.getArrivalTime() + ", " + waitingClient.getProcessingTime() + "); ");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.print("(" + waitingClient.getID() + ", " + waitingClient.getArrivalTime() + ", " + waitingClient.getProcessingTime() + "); ");
                output.append("(" + waitingClient.getID() + ", " + waitingClient.getArrivalTime() + ", " + waitingClient.getProcessingTime() + "); ");
            }

            try {
                myWriter.write(System.lineSeparator());
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println();
            output.append(System.lineSeparator());

            // check if the waitingClients list is empty
            if (waitingClients.size() == 0) {
                waitingClientsEmpty = true;
            }

            // print the statuses of the queues
            for (int j = 0; j < Q; j++) {

                try {
                    myWriter.write("Queue " + queues[j].getQueueNumber() + ": ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                output.append("Queue " + queues[j].getQueueNumber() + ": ");
                System.out.print("Queue " + queues[j].getQueueNumber() + ": ");
                if (queues[j].getNrClients() > 0) {
                    for (int k = 0; k < queues[j].getNrClients(); k++) {

                        try {
                            myWriter.write("(" + queues[j].getClientList().get(k).getID() + ", " + queues[j].getClientList().get(k).getArrivalTime() + ", " + queues[j].getClientList().get(k).getProcessingTime() + "); ");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        output.append("(" + queues[j].getClientList().get(k).getID() + ", " + queues[j].getClientList().get(k).getArrivalTime() + ", " + queues[j].getClientList().get(k).getProcessingTime() + "); ");
                        System.out.print("(" + queues[j].getClientList().get(k).getID() + ", " + queues[j].getClientList().get(k).getArrivalTime() + ", " + queues[j].getClientList().get(k).getProcessingTime() + "); ");
                    }
                } else if (queues[j].getNrClients() == 0) {

                    try {
                        myWriter.write("closed");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    output.append("closed");
                    System.out.print("closed");
                }

                try {
                    myWriter.write(System.lineSeparator());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                output.append(System.lineSeparator());
                System.out.println();
            }

            // compute the peak hours for the queues
            int nbCustomers = 0;
            for (int j = 0; j < Q; j++) {
                nbCustomers += queues[j].getNrClients();
            }
            if (nbCustomers > maxCustomers) {
                maxCustomers = nbCustomers;
                peakHour = clock.getTime();
            }

            // check if all the queues are empty
            queuesEmpty = true;
            for (int j = 0; j < Q; j++) {
                if (queues[j].getClientList().size() > 0) {
                    queuesEmpty = false;
                    break;
                }
            }

            out.add(output);

            try {
                Thread.sleep(1000); // wait for one second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // stopping the queue threads
        for (int i = 0; i < Q; i++) {
            queues[i].stop();
        }
        // stopping the clock thread
        clock.stopClock();
        int stopTime = clock.getTime() - 1;

        try {
            myWriter.write("Simulation stopped at time " + stopTime + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
        output.append("Simulation stopped at time " + stopTime + System.lineSeparator());
        System.out.println("Simulation stopped at time " + stopTime);
        // print the average waiting time

        try {
            myWriter.write("Average waiting time: " + round(computeAverageWaitingTime(), 2) + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
        output.append("Average waiting time: " + round(computeAverageWaitingTime(), 2) + System.lineSeparator());
        System.out.println("Average waiting time: " + round(computeAverageWaitingTime(), 2));
        // print the average service time

        try {
            myWriter.write("Average service time: " + round(computeAverageServiceTime(), 2) + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
        output.append("Average service time: " + round(computeAverageServiceTime(), 2) + System.lineSeparator());
        System.out.println("Average service time: " + round(computeAverageServiceTime(), 2));
        // print peak hour

        try {
            myWriter.write("Peak hour: " + maxCustomers + " customers at time " + peakHour + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
        output.append("Peak hour: " + maxCustomers + " customers at time " + peakHour + System.lineSeparator());
        System.out.println("Peak hour: " + maxCustomers + " customers at time " + peakHour);

        try {
            myWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        isRunning = false;
        out.add(output);
    }

}

