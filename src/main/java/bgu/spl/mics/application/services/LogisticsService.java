package bgu.spl.mics.application.services;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.FinishBroadcast;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;


import bgu.spl.mics.MicroService;

import java.util.Vector;
import java.util.concurrent.CountDownLatch;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
	private CountDownLatch countDownLatch= BookStoreRunner.counter;


	public LogisticsService(String name) {
		super(name);
	}
	@Override
	protected void initialize() {
		subscribeEvent(DeliveryEvent.class, (de)-> {
			Future<Future<DeliveryVehicle>> deliveryVehicleFuture = sendEvent(new AcquireVehicleEvent(de.getCustomer()));
			if (deliveryVehicleFuture!=null) {
				Future<DeliveryVehicle> myDeliveryVehicleFuture = deliveryVehicleFuture.get();

				DeliveryVehicle deliveryVehicle = myDeliveryVehicleFuture.get();
				if (deliveryVehicle != null) {
					deliveryVehicle.deliver(de.getCustomer().getAddress(), de.getCustomer().getDistance());
					sendEvent(new ReleaseVehicleEvent(deliveryVehicle));

				} else
					this.complete(de, null);
			}
			else
				this.complete(de,null);

		} );

		subscribeBroadcast(FinishBroadcast.class,(finishBroadcast)->{
			terminate();
		});
		countDownLatch.countDown();
	}

}
