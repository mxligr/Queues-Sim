package sample;

public class Client {
    private final int ID;
    private final int arrivalTime;        // time that the client goes to the queue
    private int processingTime;     // the client will stay at the front of the queue for this time
    private int waitingTime;        // waiting time for a client until he goes to the front of the queue
    private int serviceTime;        // waiting time + processing time

    public Client(int ID, int arrivalTime, int processingTime) {
        this.ID = ID;
        this.arrivalTime = arrivalTime;
        this.processingTime = processingTime;
        this.waitingTime = 0;
        this.serviceTime = 0;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(int processingTime) {
        this.processingTime = processingTime;
    }

    public int getID() {
        return ID;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }
}

