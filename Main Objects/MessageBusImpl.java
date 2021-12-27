package bgu.spl.mics;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.services.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * the {@link MessageBusImpl} is a singleton class, that connect betwen all the microServices.
 * its contains 4 hashmaps fields, that help manage all the information.
 * @param eventLine: every message type, has a microService queue.
 * @param broadcastLine: every message type, has a microService list.
 * @param microLine: every microService, has a message queue.
 * @param futureLine: every Event, has its Future.
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private ConcurrentHashMap<Class<? extends Message>, BlockingQueue<MicroService>> eventLine;
	private ConcurrentHashMap<Class<? extends Message>, CopyOnWriteArrayList<MicroService>> broadcastLine;
	private ConcurrentHashMap<MicroService, BlockingQueue<Message>> microLine;
	private ConcurrentHashMap<Event, Future> futureLine;
	private Object lockE;

	/**
	 * a singleton implementation
	 */
	private static MessageBusImpl instance = null;
	public static MessageBusImpl getInstance(){
		if (instance == null)
			instance = new MessageBusImpl();
		return instance;
	}
	/**
	 * a private Constructor
	 */
	private MessageBusImpl(){
		//creating the eventLine field and put the 3 event type inside.
		eventLine = new ConcurrentHashMap<>();
		eventLine.put(AttackEvent.class, new LinkedBlockingQueue<>());
		eventLine.put(DeactivationEvent.class, new LinkedBlockingQueue<>());
		eventLine.put(BombDestroyerEvent.class, new LinkedBlockingQueue<>());

		//creating the broadcastLine field and put the 3 Broadcast type inside.
		broadcastLine = new ConcurrentHashMap<>();
		broadcastLine.put(AttackBroadcast.class, new CopyOnWriteArrayList<>());
		broadcastLine.put(DeactivationBroadcast.class, new CopyOnWriteArrayList<>());
		broadcastLine.put(BombDestroyerBroadcast.class, new CopyOnWriteArrayList<>());

		microLine = new ConcurrentHashMap<>();
		futureLine = new ConcurrentHashMap<>();

		lockE = new Object();
	}
	/**
	 * Subscribes to events of type {@code type}
	 * add a microService to the event type queue in the eventLine,
	 * and create a new pair if the eventLine doesnt contain the type of message(key).
	 * @param <T>      The type of result expected for the subscribed event.
	 * @param type     The {@link Class} representing the type of event to
	 *                 subscribe to.
	 */
	@Override
	public  <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
			if (!eventLine.containsKey(type))
				eventLine.put(type, new LinkedBlockingQueue<>());

			Queue<MicroService> q = eventLine.get(type);
			if (!q.contains(m))
				q.add(m);
	}

	/**
	 * Subscribes to broadcast of type {@code type}
	 * add a microService to the broadcast type list in the broadcastLine,
	 * and create a new pair if the broadcastLine doesnt contain the type of message(key).
	 * @param type     The {@link Class} representing the type of broadcast to
	 *                 subscribe to.
	 */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (!broadcastLine.containsKey(type))
				broadcastLine.put(type, new CopyOnWriteArrayList<>());

		List<MicroService> l = broadcastLine.get(type);
		if (!l.contains(m))
			l.add(m);
    }

	/**
	 * Completes the received request {@code e} with the result {@code result}
	 * and resolve it.
	 * <p>
	 * @param <T>    The type of the expected result of the processed event
	 *               {@code e}.
	 * @param e      The event to complete.
	 * @param result The result to resolve the relevant Future object.
	 *               {@code e}.
	 */
	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		Future fut = futureLine.get(e);
		fut.resolve(result);
	}

	/**
	 * gets the microServices that registered to the broadcast broadcast message {@code b} ,
	 * and add b to their queue to all the services subscribed to it.
	 * then notify all microService who wait for new message.
	 * <p>
	 * @param b The broadcast message to send
	 */
	@Override
	public void sendBroadcast(Broadcast b) {
		List<MicroService> microServices = broadcastLine.get(b.getClass());
		for (MicroService m : microServices) {
			microLine.get(m).add(b);
		}
		synchronized (lockE) {
			lockE.notifyAll();
		}
	}

	/**
	 * put an event {@code e} in the right microservice (by the rubin order) and create a {@link Future<T>}
	 * and put the pair event {@code e} and future {@link Future<T>} in future line.
	 * then notify all microService who wait for new message.
	 *
	 * notice that only leia uses this function so the synchronization(this) doest effect the run time.
	 * we used it in case that there will be tests in which multi threads will use this function.
	 *
	 * <p>
	 * @param <T>       The type of the expected result of the request
	 *                  {@code e}
	 * @param e         The event to send
	 * @return  		{@link Future<T>} object that may be resolved later by a different
	 *         			micro-service processing this event.
	 * 	       			null in case no micro-service has subscribed to {@code e.getClass()}.
	 */
	@Override
	public synchronized  <T> Future<T> sendEvent(Event<T> e) {
		if (!eventLine.containsKey(e.getClass()) ||eventLine.get(e.getClass()).isEmpty())
			return null;

		BlockingQueue<MicroService> queue = eventLine.get(e.getClass());
		MicroService first = queue.poll();
		queue.add(first);
		microLine.get(first).add(e);

		Future<T> future = new Future<>();
		futureLine.put(e, future);

		synchronized (lockE) {
			lockE.notifyAll();
		}
		return future;
	}

	/**
	 * create a queue for a microservice and add the pair to the microLine
	 * @param m the micro-service to create a queue for.
	 */
	@Override
	public void register(MicroService m) {
		if (!microLine.containsKey(m)) {
			BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
			microLine.put(m, queue);
		}
	}

	/**
	 * remove from the microLine the pair with the key m.
	 * remove m from the microService queue and list of the proper message type in eventLine and BroadcastLine.
	 * then, only if the microLine is empty, which means the run is about to be over, clears the future line.
	 *
	 * @param m the micro-service to unregister.
	 */
	@Override
	public void unregister(MicroService m) {
		if (microLine.containsKey(m))
			microLine.remove(m);

		for (Map.Entry<Class<? extends Message>, BlockingQueue<MicroService>> it : eventLine.entrySet()) {
			BlockingQueue<MicroService> queue = it.getValue();
			if (queue.contains(m)) {
				queue.remove(m);
				break;
			}
		}

		for (Map.Entry<Class<? extends Message>, CopyOnWriteArrayList<MicroService>> it : broadcastLine.entrySet()) {
			List<MicroService> list = it.getValue();
			if (list.contains(m)) {
				list.remove(m);
				break;
			}
		}
		if (microLine.isEmpty())
			futureLine.clear();
	}

	/**
	 * This is a blocking method!
	 * if the microService's queue in the microLine is empty wait for notify about a new message.
	 * then poll the first message from the line and return it.
	 *
	 * @param m The micro-service requesting to take a message from its message
	 *          queue.
	 * @return Message (event or broadcast)
	 */
	@Override
	public Message awaitMessage(MicroService m) {
		BlockingQueue<Message> queue = microLine.get(m);
		synchronized (lockE) {
			while (queue.isEmpty()) {
				try {
					lockE.wait();
				} catch (InterruptedException ignored) {
				}
			}
		}
		Message newMessage = queue.poll();

		return newMessage;
	}
}
