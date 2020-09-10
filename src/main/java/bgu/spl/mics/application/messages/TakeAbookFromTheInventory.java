package bgu.spl.mics.application.messages;

import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import bgu.spl.mics.Event;

public class TakeAbookFromTheInventory implements Event<OrderResult> {

    private Customer customer;
    private String bookTitle;


    public TakeAbookFromTheInventory (String bookTitle,Customer customer){
        this.bookTitle=bookTitle;
        this.customer=customer;

    }

    public String getBookTitle(){
        return this.bookTitle;
    }

    public Customer getCustomer(){
        return this.customer;
    }



}
