package sample;

import java.util.ArrayList;
import java.util.List;

public class QueueThread implements Runnable {

    private List<Client> clientList;    // list of clients that are in the queue
    private final int queueNumber;            // index of the queue
    private boolean isRunning;          // the queue thread will run while isRunning is true
    private int nrClients;              // number of clients in the queue
    private int totalClients;           // number of total clients that were in the queue
    private int totalWaitingTime;
    private final Clock clock;
    private final Thread queue;

    public QueueThread(int queueNumber, Clock clock) {
        this.queueNumber = queueNumber;
        this.clock = clock;
        this.clientList = new ArrayList<>();
        this.isRunning = false;
        this.nrClients = 0;
        this.totalClients = 0;
        this.totalWaitingTime = 0;
        this.queue = new Thread(this);

        start();
    }

    public void start() {
        this.isRunning = true;
        queue.start();
    }

    public void stop() {
        this.isRunning = false;
    }

    public void addClient(Client client) {
        this.clientList.add(client);
        nrClients++;
        totalClients++;
        //System.out.println("Client " + client.getID() + " arrived at queue " + this.queueNumber + " at time " + clock.getTime());
    }

    public void removeClient(Client client) {
        this.clientList.remove(client);
        nrClients--;
        //System.out.println("Client " + client.getID() + " left queue " + this.queueNumber + " at time " + clock.getTime());
    }

    public int getServiceTime() {
        int serviceTime = 0;
        for (Client client : clientList) {
            serviceTime += client.getProcessingTime();
        }
        return serviceTime;
    }

    public int getQueueNumber() {
        return queueNumber;
    }

    public List<Client> getClientList() {
        return clientList;
    }

    public int getNrClients() {
        return nrClients;
    }

    @Override
    public void run() {
        while (isRunning || clientList.size() > 0) {
            if (!clientList.isEmpty()) {
                Client currentClient = this.clientList.get(0);
                int serviceTime = currentClient.getProcessingTime();
                totalWaitingTime += clock.getTime() - currentClient.getArrivalTime();
                currentClient.setWaitingTime(clock.getTime() - currentClient.getArrivalTime());
                currentClient.setServiceTime(currentClient.getWaitingTime() + currentClient.getProcessingTime());
                currentClient.setProcessingTime(serviceTime--);
                while(serviceTime > 0) {
                    currentClient.setProcessingTime(serviceTime--);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                removeClient(currentClient);
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

