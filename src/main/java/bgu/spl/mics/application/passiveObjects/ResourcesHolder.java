package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {

	private  Vector<DeliveryVehicle> vehicles;
	private Queue<Future> futureVehiclesList;


	//create singelton Inventory

	private static class ResourcesHolderH {
		private static ResourcesHolder instance = new ResourcesHolder();
	}

	private ResourcesHolder(){
		vehicles=new Vector<>();
		this.futureVehiclesList=new ConcurrentLinkedQueue<>();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static ResourcesHolder getInstance() {
		return ResourcesHolderH.instance;
	}

	/**
	 * Tries to acquire a vehicle and gives a future object which will
	 * resolve to a vehicle.
	 * <p>
	 * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a
	 * 			{@link DeliveryVehicle} when completed.
	 */
	public Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> output=new Future<>();
		if(!vehicles.isEmpty()) {
			output.resolve(vehicles.firstElement());
		}
		else{
			this.futureVehiclesList.add(output);
		}
		return output;

}

	/**
	 * Releases a specified vehicle, opening it again for the possibility of
	 * acquisition.
	 * <p>
	 * @param vehicle	{@link DeliveryVehicle} to be released.
	 */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		synchronized (this.futureVehiclesList){
			if(!this.futureVehiclesList.isEmpty()) {
				this.futureVehiclesList.poll().resolve(vehicle);
			}
			else
				this.vehicles.add(vehicle);

			}
		}


	/**
	 * Receives a collection of vehicles and stores them.
	 * <p>
	 * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
	 */
	public void load(DeliveryVehicle[] vehicles) {
		for(int i=0;i<vehicles.length;i++)
			this.vehicles.add(vehicles[i]);
	}

}
