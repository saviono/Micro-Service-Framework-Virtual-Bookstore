package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

public class OrderSchedule implements Serializable {
    private Integer tick;
    private String bookTitle;

    public OrderSchedule(String bookTitle, Integer time){
        this.bookTitle=bookTitle;
        this.tick=time;

    }

    public String getBookTitle (){
        return this.bookTitle;
    }

    public Integer getTime (){
        return this.tick;
    }


    public void getTime (Integer i){
        this.tick=i;
    }


}
