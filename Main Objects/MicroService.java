package bgu.spl.mics;

import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * The MicroService is an abstract class that any micro-service in the system
 * must extend. The abstract MicroService class is responsible to get and
 * manipulate the singleton {@link MessageBus} instance.
 * <p>
 * Derived classes of MicroService should never directly touch the message-bus.
 * Instead, they have a set of internal protected wrapping methods (e.g.,
 * {@link #sendBroadcast(bgu.spl.mics.Broadcast)}, {@link #sendBroadcast(bgu.spl.mics.Broadcast)},
 * etc.) they can use. When subscribing to message-types,
 * the derived class also supplies a {@link Callback} that should be called when
 * a message of the subscribed type was taken from the micro-service
 * message-queue (see {@link MessageBus#register(bgu.spl.mics.MicroService)}
 * method). The abstract MicroService stores this callback together with the
 * type of the message is related to.
 * 
 * Only private fields and methods may be added to this class.
 * <p>
 */
public abstract class MicroService implements Runnable { 
    protected String name;
    protected  ConcurrentHashMap<Class<? extends Message>, Callback<?>> callbackList;
    protected ConcurrentHashMap<Event, Future> futureList;


    /**
     * @param name the micro-service name (used mainly for debugging purposes -
     *             does not have to be unique)
     */
    public MicroService(String name) {
    	this.name = name;
    	callbackList = new ConcurrentHashMap<>();
        futureList = new ConcurrentHashMap<>();

    }

    /**
     * Subscribes to events of type {@code type} with the callback
     * {@code callback}. This means two things:
     * 1. Subscribe to events in the singleton event-bus using the supplied
     * {@code type}
     * 2. Store the {@code callback} so that when events of type {@code type}
     * are received it will be called.
     * <p>
     * For a received message {@code m} of type {@code type = m.getClass()}
     * calling the callback {@code callback} means running the method
     * {@link Callback#call(java.lang.Object)} by calling
     * {@code callback.call(m)}.
     * <p>
     * @param <E>      The type of event to subscribe to.
     * @param <T>      The type of result expected for the subscribed event.
     * @param type     The {@link Class} representing the type of event to
     *                 subscribe to.
     * @param callback The callback that should be called when messages of type
     *                 {@code type} are taken from this micro-service message
     *                 queue.
     */
    protected final <T, E extends Event<T>> void subscribeEvent(Class<E> type, Callback<E> callback) {
    	MessageBusImpl bus = MessageBusImpl.getInstance();
    	bus.subscribeEvent(type, this);
    	callbackList.put(type,callback);
    }

    /**
     * Subscribes to broadcast message of type {@code type} with the callback
     * {@code callback}. This means two things:
     * 1. Subscribe to broadcast messages in the singleton event-bus using the
     * supplied {@code type}
     * 2. Store the {@code callback} so that when broadcast messages of type
     * {@code type} received it will be called.
     * <p>
     * For a received message {@code m} of type {@code type = m.getClass()}
     * calling the callback {@code callback} means running the method
     * {@link Callback#call(java.lang.Object)} by calling
     * {@code callback.call(m)}.
     * <p>
     * @param <B>      The type of broadcast message to subscribe to
     * @param type     The {@link Class} representing the type of broadcast
     *                 message to subscribe to.
     * @param callback The callback that should be called when messages of type
     *                 {@code type} are taken from this micro-service message
     *                 queue.
     */
    protected final <B extends Broadcast> void subscribeBroadcast(Class<B> type, Callback<B> callback) {
        MessageBusImpl bus = MessageBusImpl.getInstance();
        bus.subscribeBroadcast(type, this);
        callbackList.put(type,callback);
    }

    /**
     * Sends the event {@code e} using the message-bus and receive a {@link Future<T>}
     * object that may be resolved to hold a result. This method must be Non-Blocking since
     * there may be events which do not require any response and resolving.
     * <p>
     * @param <T>       The type of the expected result of the request
     *                  {@code e}
     * @param e         The event to send
     * @return  		{@link Future<T>} object that may be resolved later by a different
     *         			micro-service processing this event.
     * 	       			null in case no micro-service has subscribed to {@code e.getClass()}.
     */
    protected final <T> Future<T> sendEvent(Event<T> e) {
        MessageBusImpl bus = MessageBusImpl.getInstance();
        Future fut = bus.sendEvent(e);
        futureList.put(e,fut);
        return fut;
    }

    /**
     * A Micro-Service calls this method in order to send the broadcast message {@code b} using the message-bus
     * to all the services subscribed to it.
     * <p>
     * @param b The broadcast message to send
     */
    protected final void sendBroadcast(Broadcast b) {
        MessageBusImpl bus = MessageBusImpl.getInstance();
        bus.sendBroadcast(b);
    }

    /**
     * Completes the received request {@code e} with the result {@code result}
     * using the message-bus.
     * <p>
     * @param <T>    The type of the expected result of the processed event
     *               {@code e}.
     * @param e      The event to complete.
     * @param result The result to resolve the relevant Future object.
     *               {@code e}.
     */
    protected final <T> void complete(Event<T> e, T result) {
        MessageBusImpl bus = MessageBusImpl.getInstance();
        bus.complete(e,result);
    }

    /**
     * this method is called once when the event loop starts.
     */
    protected abstract void initialize();

    /**
     * Signals the event loop that it must terminate after handling the current
     * message.
     */
    protected final void terminate() {
        Flags flags = Flags.getInstance();
        flags.setCanTerminate(true);
        synchronized (Flags.class){
            Flags.class.notifyAll();
        }
    }

    /**
     * @return the name of the service - the service name is given to it in the
     *         construction time and is used mainly for debugging purposes.
     */
    public final String getName() {
        return name;
    }

    /**
     * The entry point of the micro-service.
     * first of all the microService register and initialize itself,
     * the function is devided to 2 part.
     * part 1: (all the microServices except of leia)
     * taking messages bt the awaits function and execute the corresponding callback.
     * repeat this action until receiving a broadcast type message.
     * then waiting for the isCanTerminate flag to turn true, and then terminate gracefully, and update the dairy.
     *
     * part 2: (only leia)
     * create and send all the attack events, and wait for them to be over.
     * then create and send attackBroadcast and Deactivation Event.
     * after the DeactivationEvent is over, sending a DeactivationBroadcast, and BombDestroyerEvent.
     * after its over, send BombBroadcast and use the terminate function.
     * then terminate Gracefully, and update the dairy.
     */
    @Override
    public final void run() {
    	MessageBusImpl bus = MessageBusImpl.getInstance();
    	bus.register(this);
        this.initialize();

        if (this.getClass() != LeiaMicroservice.class){
            boolean flag = true;
            while (flag) {

                Message newMessage;
                newMessage = bus.awaitMessage(this);

                Callback callback = callbackList.get(newMessage.getClass());
                callback.call(newMessage);

                synchronized (MicroService.class) {
                    MicroService.class.notifyAll();
                }
                if (newMessage instanceof Broadcast){
                    flag = false;
                }
            }
            Diary diary= Diary.getInstance();
            Flags flags = Flags.getInstance();
            while (!flags.isCanTerminate()){
                synchronized (Flags.class){
                    try {
                        Flags.class.wait();
                    }catch (InterruptedException ignored){}
                }
            }
            diary.setTerminiate(this);
        }

        else {
            synchronized (MicroService.class){
                Attack[] attacks = ((LeiaMicroservice) this).getAttacks();
                AttackEvent[] att = new AttackEvent[attacks.length];
                for (int i = 0; i < attacks.length; i++) {
                    att[i] = new AttackEvent();
                    att[i].setAttack(attacks[i]);
                    this.sendEvent(att[i]);
            }

                Diary diary = Diary.getInstance();
                Flags flags = Flags.getInstance();

                while (attacks.length != diary.getTotalAttacks()) {
                    try {
                        MicroService.class.wait();
                    } catch (InterruptedException ignored) {}
                }
                DeactivationEvent deactivationEvent = new DeactivationEvent(((LeiaMicroservice) this).getdTime());

                AttackBroadcast attackBroadcast = new AttackBroadcast();
                this.sendBroadcast(attackBroadcast);

                this.sendEvent(deactivationEvent);

                while(!flags.isDeactivated()) {
                    try {
                        MicroService.class.wait();
                    } catch (InterruptedException ignored) {
                    }
                }
                    DeactivationBroadcast deactivationBroadcast = new DeactivationBroadcast();
                    this.sendBroadcast(deactivationBroadcast);

                BombDestroyerEvent bombDestroyerEvent = new BombDestroyerEvent(((LeiaMicroservice) this).getbTime());
                    this.sendEvent(bombDestroyerEvent);

                    while(!flags.isBombed()) {
                    try {
                        MicroService.class.wait();
                    } catch (InterruptedException ignored) { }
                }
                BombDestroyerBroadcast bombDestroyerBroadcast = new BombDestroyerBroadcast();
                this.sendBroadcast(bombDestroyerBroadcast);

                synchronized (CallbackBombDestroyerBroadcast.class) {
                    while (!flags.isAllUnregistered()) {
                        try {
                            CallbackBombDestroyerBroadcast.class.wait();
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
                bus.unregister(this);

                this.terminate();
                diary.setTerminiate(this);
            }
        }
    }
}
