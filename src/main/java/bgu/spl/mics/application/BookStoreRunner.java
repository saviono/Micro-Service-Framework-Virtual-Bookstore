package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.*;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;


/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static CountDownLatch counter;


    public static void main(String[] args) {
        Gson gson = new Gson();

        String jsonInput = args[0];
        String customersInput = args [1];
        String booksLeftInput = args [2];
        String orderReceipts = args [3];
        String moneyRegisterInput = args [4];

        try{
            JsonReader jReader = new JsonReader(new FileReader(jsonInput));
            Jparser jParser = gson.fromJson(jReader, Jparser.class);
            Inventory inv = Inventory.getInstance();
            inv.load(jParser.getInitialInventory());
            ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
            resourcesHolder.load(jParser.initialResources[0].vehicles);

            Vector<Customer> customersList = new Vector<>();
            for (int i=0;i<jParser.getServices().getCustomers().length;i++){
                Customer temp = jParser.getServices().getCustomers()[i];
                customersList.add(new Customer(temp.getId(),temp.getName(),temp.getAddress(),temp.getDistance(),temp.getOrderSchedule(),temp.getCreditCard().getAmount(),temp.getCreditCard().getNumber()));
            }

            counter= new CountDownLatch(jParser.services.getNumOfMicroServices());
            Vector<Thread> threadVector=new Vector<>();

            int numOfApiServices = jParser.services.getCustomers().length;
            for (int i=1;i<=numOfApiServices;i++){
                APIService m = new APIService("API Service "+ i, customersList.get(i-1));
                Thread t = new Thread(m);
                threadVector.add(t);

            }


            int numOfSellingServices = jParser.services.getSelling();
            for (int i=1;i<=numOfSellingServices;i++){
                SellingService m = new SellingService("Selling Service "+ i);
                Thread t = new Thread(m);
                threadVector.add(t);
            }

            int numOfInventory = jParser.services.getInventoryService();
            for (int i=1;i<=numOfInventory;i++){
                InventoryService m = new InventoryService("Inventory Service "+ i);
                Thread t = new Thread(m);
                threadVector.add(t);
            }

            int numOfLogisticsServices = jParser.services.getLogistics();
            for (int i=1;i<=numOfLogisticsServices;i++){
                LogisticsService m = new LogisticsService("Logistic Service "+ i);
                Thread t = new Thread(m);
                threadVector.add(t);
            }

            int numOfResourceServices = jParser.services.getResourcesService();
            for (int i=1;i<=numOfResourceServices;i++){
                ResourceService m = new ResourceService("Resource Service "+ i);
                Thread t = new Thread(m);
                threadVector.add(t);
            }

            Time time = jParser.services.getTime();
            TimeService timeServiceProcess = new TimeService(time);

            Thread t = new Thread(timeServiceProcess);
            threadVector.add(t);

            for(int i=0;i<threadVector.size();i++)
            {
                threadVector.get(i).start();
            }


            try {
                for (int i = 0; i < threadVector.size(); i++) {
                    Thread thread = threadVector.get(i);
                    thread.join();
                    System.out.println("dassd");

                }
            }catch (Exception e){

            }

            HashMap <Integer,Customer> customers = new HashMap<>();
            for(Customer c : customersList){
                customers.put(c.getId(),c);
            }

            try{
                FileOutputStream fout = new FileOutputStream(customersInput);
                ObjectOutputStream oos = new ObjectOutputStream(fout);
                oos.writeObject(customers);
                fout.close();
                oos.close();
            }
            catch(Exception e){
            }

            try{
                FileOutputStream fout = new FileOutputStream(moneyRegisterInput);
                ObjectOutputStream oos = new ObjectOutputStream(fout);
                oos.writeObject(MoneyRegister.getInstance());
                fout.close();
                oos.close();
            }
            catch(Exception e){
            }


        }

        catch (Exception e){
            System.out.println("Exception:" + e);
        }

        Inventory.getInstance().printInventoryToFile(booksLeftInput);
        MoneyRegister.getInstance().printOrderReceipts(orderReceipts);


    }

}


