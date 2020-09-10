package bgu.spl.mics.application.passiveObjects;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory implements Serializable {
	private  Vector<BookInventoryInfo> inventory;

	private static class InventoryHolder {
		private static Inventory instance = new Inventory();
	}

	private Inventory(){
		inventory=new Vector<>();

	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Inventory getInstance() {

		return InventoryHolder.instance;
	}

	/**
	 * Initializes the store inventory. This method adds all the items given to the store
	 * inventory.
	 * <p>
	 * @param inventory 	Data structure containing all data necessary for initialization
	 * 						of the inventory.
	 */
	public void load (BookInventoryInfo[ ] inventory ) {
		for(int i=0;i<inventory.length;i++) {
			this.inventory.add(new BookInventoryInfo(inventory[i].getBookTitle(),inventory[i].getAmountInInventory(),inventory[i].getPrice()));
		}

	}
	/**
	 * Attempts to take one book from the store.
	 * <p>
	 * @param book 		Name of the book to take from the store
	 * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
	 * 			The first should not change the state of the inventory while the
	 * 			second should reduce by one the number of books of the desired type.
	 */
	public OrderResult take (String book) {
		int index = findIndexOfABook(book);
		if (index != -1) {
			boolean ans = this.inventory.get(index).getSemaphore().tryAcquire();
			if (ans) {
				this.inventory.get(index).setAmountInInventory();
				return OrderResult.SUCCESSFULLY_TAKEN;

			}
		}

		return OrderResult.NOT_IN_STOCK;
}

	private int findIndexOfABook(String book){
		for(int i=0;i<this.inventory.size();i++){
			if(this.inventory.get(i).getBookTitle().equals(book))
				return i;
		}
		return -1;

	}


	/**
	 * Checks if a certain book is available in the inventory.
	 * <p>
	 * @param book 		Name of the book.
	 * @return the price of the book if it is available, -1 otherwise.
	 */
	public synchronized int checkAvailabiltyAndGetPrice(String book) {
		int index=findIndexOfABook(book);
		if(index!=-1) {
			if (inventory.get(index).getAmountInInventory() > 0)
				return this.inventory.get(index).getPrice();
		}
		return -1;
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
	 * should be the titles of the books while the values (type {@link Integer}) should be
	 * their respective available amount in the inventory.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printInventoryToFile(String filename){
		HashMap<String,Integer> output = new HashMap<>();
		for(BookInventoryInfo book : inventory){
			output.put(book.getBookTitle(),book.getAmountInInventory());
		}

		try{
			FileOutputStream fout = new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(output);
			fout.close();
			oos.close();
		}
		catch(Exception e){
		}
	}

}
