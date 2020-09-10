package bgu.spl.mics;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.*;
import java.util.Set;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private HashMap<MicroService,LinkedBlockingQueue<Message>> hashMapMessageBus;
	private ConcurrentHashMap<Class<? extends Event<?>>, ConcurrentLinkedQueue<MicroService>> hashMapSubscribeEvents;
	private ConcurrentHashMap<Class<? extends Broadcast>,Vector <MicroService>> hashMapSubscribeBroadcast;
	private ConcurrentHashMap<Event<?>,Future<?>> hashMapFuture;


	private static class MessageBusImplHolder{
		private static MessageBusImpl instance=new MessageBusImpl();

	}
	private MessageBusImpl(){
		hashMapMessageBus=new HashMap<>();
		hashMapSubscribeEvents=new ConcurrentHashMap<>();
		hashMapSubscribeBroadcast= new ConcurrentHashMap<>();
		hashMapFuture= new ConcurrentHashMap<>();


	}
	public static MessageBus getInstance(){
		return MessageBusImplHolder.instance;
	}

	@Override
	public synchronized  <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {

		ConcurrentLinkedQueue<MicroService> microServicesQueue = new ConcurrentLinkedQueue<>();
		try {
			microServicesQueue.add(m);
			ConcurrentLinkedQueue<MicroService>	microServicesQueue1=hashMapSubscribeEvents.putIfAbsent(type, microServicesQueue);
			if(microServicesQueue1!=null) {
				microServicesQueue1.add(m);
			}
		}
		catch(Exception e){

		}

	}

	@Override
	public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		Vector<MicroService> microServicesVector = new Vector<>();
		microServicesVector.add(m);
		Vector<MicroService> microServicesVector1 = hashMapSubscribeBroadcast.putIfAbsent(type, microServicesVector);
		if (microServicesVector1 != null) {
			microServicesVector1.add(m);
		}

	}

	@Override
	public <T> void complete(Event<T> e, T result) {////
		synchronized (e) {
			Future<T> future = (Future<T>) hashMapFuture.get(e);
			if (future != null) {
				future.resolve(result);

			}
		}

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		Vector<MicroService> microServicesVector= hashMapSubscribeBroadcast.get(b.getClass());
		if(microServicesVector!=null){
			for(int i=0;i<microServicesVector.size();i++){
				try {
					MicroService temp = microServicesVector.get(i);
					LinkedBlockingQueue<Message> microServicesQueue = hashMapMessageBus.get(temp);
					synchronized (microServicesQueue) {
						microServicesQueue.put(b);
					}
				}
				catch(Exception e){

				}
			}
		}
	}


	@Override
	public  <T> Future<T> sendEvent(Event<T> e) {
		synchronized (e) {
			ConcurrentLinkedQueue<MicroService> microServicesQueue = hashMapSubscribeEvents.get(e.getClass());
			if (microServicesQueue == null || microServicesQueue.isEmpty()) {
				return null;
			}
			synchronized (microServicesQueue) {
				try {
					MicroService temp;
					temp = microServicesQueue.poll();
					hashMapMessageBus.get(temp).put(e);
					hashMapFuture.put(e, new <T>Future<T>());
					microServicesQueue.add(temp);
				}

				catch (Exception ex) {
				}
				return (Future<T>) (hashMapFuture.get(e));
			}
		}
	}


	@Override
	public  void register(MicroService m) {
		LinkedBlockingQueue<Message> microServiceQueue = new LinkedBlockingQueue<>();
		hashMapMessageBus.putIfAbsent(m, microServiceQueue);

	}

	@Override
	public  void unregister(MicroService m) {
		Set<Class<? extends Broadcast>> setBrodcastTypes = hashMapSubscribeBroadcast.keySet();
		for (Class<? extends Broadcast> b : setBrodcastTypes) {
			Vector<MicroService> v = hashMapSubscribeBroadcast.get(b);
			if(v!=null)
				v.remove(m);

		}

		Set<Class<? extends Event<?>>> setEventTypes = hashMapSubscribeEvents.keySet();
		for (Class<? extends Event<?>> e : setEventTypes) {
			ConcurrentLinkedQueue<MicroService> queue = hashMapSubscribeEvents.get(e);
			if(queue!=null)
				queue.remove(m);
			synchronized (queue) {
				queue.remove(m);

			}
		}

		LinkedBlockingQueue<Message> messageLinkedBlockingQueue = hashMapMessageBus.get(m);
		while (!messageLinkedBlockingQueue.isEmpty()) {
			try {
				Message message = messageLinkedBlockingQueue.take();
				if(hashMapFuture.contains(message))
					this.complete((Event<?>) message, null);

			} catch (Exception e) {

			}
		}
		hashMapMessageBus.remove(m);


	}


	@Override
	public  Message awaitMessage(MicroService m) throws InterruptedException {
		Message output=null;
		if(!this.hashMapMessageBus.containsKey(m))
			throw new IllegalStateException();
		LinkedBlockingQueue<Message> microServiceQueue=hashMapMessageBus.get(m);
		try {
			output=microServiceQueue.poll(100, TimeUnit.MILLISECONDS);


		}finally {
		}

		return output;

	}

}
