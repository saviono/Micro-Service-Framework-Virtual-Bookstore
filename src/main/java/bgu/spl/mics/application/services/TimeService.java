package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.FinishBroadcast;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.mics.application.passiveObjects.Time;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 *
 */
public class TimeService extends MicroService{
	private int speed;
	private int duration;
	private AtomicInteger currentTime;
	private Timer timer;
	private CountDownLatch countDownLatch;
	private Integer locker;
	TimerTask timerTask;


	public TimeService(Time time) {
		super("time");
		this.speed=time.getSpeed();
		this.duration=time.getDuration();
		this.currentTime= new AtomicInteger(1);
		this.countDownLatch=BookStoreRunner.counter;
		this.timer = new Timer("timer");

	}

	@Override
	protected void initialize() {
		try {
			countDownLatch.await();
		}
		catch (Exception e){}
		timerTask = new TimerTask() {
			@Override
			public void run() {
				if (currentTime.get() <= duration) {
					sendBroadcast(new TickBroadcast(currentTime.get()));
					currentTime.addAndGet(1);

				}
				else {
					sendBroadcast(new FinishBroadcast());
					if(currentTime.get()>duration) {
						timer.cancel();
						timer.purge();
						timerTask.cancel();
					}
				}
			}
		};

		timer.scheduleAtFixedRate(timerTask, 0,this.speed);

		subscribeBroadcast(FinishBroadcast.class,(finishBroadcast)->{

			terminate();
		});

	}

}

































