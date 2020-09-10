package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo implements Serializable  {
	private String bookTitle;
	private AtomicInteger amount;
	private int price;
	private Semaphore semaphore;


	public BookInventoryInfo(String title,int amountInInventory,int price){
		this.bookTitle=title;
		this.amount=new AtomicInteger(amountInInventory);
		this.price=price;
		this.semaphore = new Semaphore(amountInInventory);


	}

	/**
	 * Retrieves the title of this book.
	 * <p>
	 * @return The title of this book.
	 */
	public String getBookTitle() {
		return bookTitle;
	}
	/**
	 * Retrieves the amount of books of this type in the inventory.
	 * <p>
	 * @return amount of available books.
	 */
	public int getAmountInInventory() {
		return amount.get();
	}

	public void setAmountInInventory(){
		this.amount.addAndGet(-1);
	}

	/**
	 * Retrieves the price for  book.
	 * <p>
	 * @return the price of the book.
	 */
	public int getPrice() {
		return price;
	}

	public Semaphore getSemaphore(){
		return this.semaphore;
	}


}
