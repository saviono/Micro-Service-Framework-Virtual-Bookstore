package bgu.spl.mics.application.messages;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.Event;

public class AcquireVehicleEvent implements Event<Future<DeliveryVehicle>> {

    private Customer customer;

    public AcquireVehicleEvent(Customer customer){
        this.customer=customer;


    }

    public Customer getCustomer(){
        return this.customer;

    }

}






