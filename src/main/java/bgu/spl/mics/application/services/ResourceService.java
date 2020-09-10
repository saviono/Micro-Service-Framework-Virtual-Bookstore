package bgu.spl.mics.application.services;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.FinishBroadcast;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService {

	ResourcesHolder resourcesHolder=ResourcesHolder.getInstance();
	private CountDownLatch countDownLatch= BookStoreRunner.counter;
	private Queue<Future>  futureVector;

	public ResourceService(String name) {
		super(name);
		futureVector=new ConcurrentLinkedQueue<>();

	}

	@Override
	protected void initialize() {
		subscribeEvent(AcquireVehicleEvent.class, (acqve) -> {
			Future<DeliveryVehicle> deliveryVehicleFuture=resourcesHolder.acquireVehicle();
			this.complete(acqve,deliveryVehicleFuture);
			this.futureVector.add(deliveryVehicleFuture);


		});
		subscribeEvent(ReleaseVehicleEvent.class,(relv)->{
			this.resourcesHolder.releaseVehicle(relv.getDeliveryVehicle());
			this.complete(relv,relv.getDeliveryVehicle());
		});

		subscribeBroadcast(FinishBroadcast.class,(finishBroadcast)->{
			while (!this.futureVector.isEmpty()){
				if(!this.futureVector.peek().isDone())
					this.futureVector.poll().resolve(null);
				else
					this.futureVector.poll();
			}

			terminate();
		});
		countDownLatch.countDown();


	}
}
