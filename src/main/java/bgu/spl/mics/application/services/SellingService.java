package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{
	private MoneyRegister moneyRegister=MoneyRegister.getInstance();
	private int proccessTick;
	private CountDownLatch countDownLatch= BookStoreRunner.counter;


	public SellingService(String name) {
		super(name);
		proccessTick=1;
	}

	@Override
	protected void initialize() {

		this.subscribeBroadcast(TickBroadcast.class ,(tb)-> {
			this.proccessTick = tb.getTime();
		});

		this.subscribeEvent(BookOrderEvent.class,(boe)-> {
			Future<Integer> priceFuture = sendEvent(new CheckAvailabilityEvent(boe.getBookTitle(),boe.getCustomer()));
			synchronized (boe.getCustomer()) {
				if (priceFuture != null) {
					Integer resolved = priceFuture.get();
					if ((resolved != null) && (resolved != -1) && (boe.getCustomer().getAvailableCreditAmount() >= resolved)) {
						Future<OrderResult> futureInStock = sendEvent(new TakeAbookFromTheInventory(boe.getBookTitle(), boe.getCustomer()));
						if (futureInStock != null) {
							OrderResult orderResult = futureInStock.get();
							if ((orderResult != null) && (orderResult.equals(OrderResult.SUCCESSFULLY_TAKEN))) {
								moneyRegister.chargeCreditCard(boe.getCustomer(), resolved);
								OrderReceipt orderReceipt = new OrderReceipt(0, getName(), boe.getCustomer().getId(), boe.getBookTitle(), resolved, proccessTick, boe.getOrderTick(), proccessTick);
								this.complete(boe, orderReceipt);
								moneyRegister.file(orderReceipt);
							} else {

								this.complete(boe, null);
							}

						} else {
							this.complete(boe, null);
						}
					} else {

						this.complete(boe, null);

					}
				} else {

					this.complete(boe, null);
				}
			}
		});
		subscribeBroadcast(FinishBroadcast.class,(finishBroadcast)->{
			terminate();
		});
		countDownLatch.countDown();
	}
}

