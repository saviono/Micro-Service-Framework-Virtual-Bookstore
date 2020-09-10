package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {


	private int id;
	private String name;
	private String address;
	private int distance;
	private Vector<OrderReceipt> customerReceiptList;
	private CreditCard creditCard;
	private Vector<OrderSchedule> orderSchedule;
	private AtomicInteger availableAmount;
	private int creditCardNumber;


	public Customer(int id, String name, String address, int distance,Vector<OrderSchedule> orderSchedule, int availableAmount, int creditCardNumber){
		this.id=id;
		this.name=name;
		this.address=address;
		this.distance=distance;
		this.orderSchedule = orderSchedule;
		this.availableAmount = new AtomicInteger(availableAmount);
		this.creditCardNumber = creditCardNumber;
		this.customerReceiptList = new Vector<>();

	}

	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return this.name;

	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return this.id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return distance;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public Vector<OrderReceipt> getCustomerReceiptList() {
		return customerReceiptList;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return this.availableAmount.get();
	}

	public void setCreditAmount(int creditAmount){

		this.availableAmount.getAndSet(creditAmount);
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return creditCardNumber;
	}

	public Vector<OrderSchedule> getOrderSchedule() {
		return orderSchedule;
	}

	public void addReceipt(OrderReceipt receipt) {
		   this.customerReceiptList.add(receipt);
	}

	public CreditCard getCreditCard() {
		return creditCard;
	}
}
