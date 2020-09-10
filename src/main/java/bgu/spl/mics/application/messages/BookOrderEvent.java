package bgu.spl.mics.application.messages;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.Event;


public class BookOrderEvent implements Event<OrderReceipt>{

    private Customer customer;
    private int orderTick;
    private String bookTitle;

    public BookOrderEvent(Customer customer, String bookTitle , int orderTick){
        this.customer=customer;
        this.bookTitle=bookTitle;
        this.orderTick=orderTick;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getOrderTick(){
        return orderTick;
    }

    public String getBookTitle(){
        return  bookTitle;
    }




}

