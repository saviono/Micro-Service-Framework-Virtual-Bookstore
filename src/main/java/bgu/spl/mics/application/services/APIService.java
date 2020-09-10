package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.FinishBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderSchedule;

import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;

import java.util.List;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class APIService extends MicroService{
	private Customer customer;
	private ConcurrentHashMap <Integer,LinkedBlockingQueue<String>> hashMapBookListByTime;
	private CountDownLatch countDownLatch= BookStoreRunner.counter;
	private Queue<Future<OrderReceipt>> futuresOfBooksOrderedInTheSameTick;


	public APIService(String name,Customer customer) {
		super(name);
		this.futuresOfBooksOrderedInTheSameTick=new ConcurrentLinkedQueue<>();
		this.customer = customer;
		hashMapBookListByTime=new ConcurrentHashMap<>();
		//Creates an HashMap - key; Tick, Value; A queue of books which should be order on that tick
		for (int i = 0; i < this.customer.getOrderSchedule().size(); i++) {
			LinkedBlockingQueue<String> tempBookQueue = new LinkedBlockingQueue<>();
			try {
				OrderSchedule orderSchedule = this.customer.getOrderSchedule().get(i);
				tempBookQueue.put(orderSchedule.getBookTitle());
				LinkedBlockingQueue<String> bookQueue1 = hashMapBookListByTime.putIfAbsent(orderSchedule.getTime(), tempBookQueue);
				if (bookQueue1 != null)
					bookQueue1.put(orderSchedule.getBookTitle());
			} catch (Exception e) {

			}

		}
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, (tb)-> {
			LinkedBlockingQueue<String> bookQueue=hashMapBookListByTime.get(tb.getTime());
			while ((bookQueue!=null) && (!bookQueue.isEmpty())) {
				String bookTitle = bookQueue.poll();
				BookOrderEvent bookOrderEvent = new BookOrderEvent(customer, bookTitle, tb.getTime());
				Future<OrderReceipt> futureObject = sendEvent(bookOrderEvent);
				if (futureObject != null) {
					this.futuresOfBooksOrderedInTheSameTick.add(futureObject);
				}
			}
				while (!this.futuresOfBooksOrderedInTheSameTick.isEmpty()) {
					OrderReceipt resolved = this.futuresOfBooksOrderedInTheSameTick.poll().get();
					if (resolved != null) {
						customer.addReceipt(resolved);
						sendEvent(new DeliveryEvent(this.customer));
					}
				}



		});
		subscribeBroadcast(FinishBroadcast.class,(finishBroadcast)->{
			terminate();
	});
		countDownLatch.countDown();
	}

}



