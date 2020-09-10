package bgu.spl.mics.application;


import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.Time;

public class Services {

    public Services() {
    }

    private Time time;
    private int selling;
    private int inventoryService;
    private int logistics;
    private int resourcesService;
    private Customer[] customers;

    public Time getTime() {
        return time;
    }

    public int getSelling() {
        return selling;
    }

    public int getInventoryService() {
        return inventoryService;
    }

    public int getLogistics() {
        return logistics;
    }

    public int getResourcesService() {
        return resourcesService;
    }

    public int getNumOfMicroServices(){
        int sum=customers.length+selling+inventoryService+logistics+resourcesService;
        return sum;
    }
    public Customer[] getCustomers() {
        return customers;
    }
}
