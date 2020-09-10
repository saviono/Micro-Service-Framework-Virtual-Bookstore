package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.FinishBroadcast;
import bgu.spl.mics.application.messages.TakeAbookFromTheInventory;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;

import java.util.concurrent.CountDownLatch;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{

	private Inventory inventory = Inventory.getInstance();
	private CountDownLatch countDownLatch= BookStoreRunner.counter;

	public InventoryService(String name) {
		super(name);
	}

	@Override
	protected void initialize() {

		subscribeEvent(CheckAvailabilityEvent.class,(cavl)->{
			int price=inventory.checkAvailabiltyAndGetPrice(cavl.getBookTitle());
			complete(cavl, price);
		});

		subscribeEvent(TakeAbookFromTheInventory.class,(Tbfi)->{
			OrderResult orderResult = inventory.take(Tbfi.getBookTitle());
			complete(Tbfi, orderResult);
		});

		subscribeBroadcast(FinishBroadcast.class,(finishBroadcast)->{

			terminate();
		});
		countDownLatch.countDown();

	}

}
