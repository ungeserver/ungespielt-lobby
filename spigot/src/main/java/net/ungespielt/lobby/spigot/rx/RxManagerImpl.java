package net.ungespielt.lobby.spigot.rx;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.Lists;
import de.jackwhite20.base.api.spigot.command.CommandService;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import net.ungespielt.lobby.spigot.api.rx.CommandExecution;
import net.ungespielt.lobby.spigot.api.rx.RxManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class RxManagerImpl implements RxManager {

    /**
     * The bukkit plugin manager.
     */
    private final PluginManager pluginManager;

    /**
     * The bukkit plugin.
     */
    private final Plugin plugin;

    /**
     * The command service.
     */
    private final CommandService commandService;

    /**
     * The dispose bag for all listeners and so on.
     */
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    /**
     * Create a new rx manager instance.
     *
     * @param syncScheduler         The synchronous scheduler.
     * @param asynchronousScheduler The asynchronous scheduler.
     * @param pluginManager         The plugin manager.
     * @param plugin                The plugin.
     * @param commandService        The command service.
     */
    @Inject
    public RxManagerImpl(@Named("syncScheduler") Scheduler syncScheduler, @Named("asyncScheduler") Scheduler asynchronousScheduler, PluginManager pluginManager, Plugin plugin, CommandService commandService) {
        this.pluginManager = pluginManager;
        this.plugin = plugin;
        this.commandService = commandService;

        RxJavaPlugins.setErrorHandler(throwable -> plugin.getLogger().log(Level.SEVERE, "Unhandled exception. ", throwable));

        RxJavaPlugins.setInitComputationSchedulerHandler(schedulerCallable -> syncScheduler);
        RxJavaPlugins.setInitIoSchedulerHandler(schedulerCallable -> asynchronousScheduler);
        RxJavaPlugins.setInitNewThreadSchedulerHandler(schedulerCallable -> asynchronousScheduler);
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> syncScheduler);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> asynchronousScheduler);
        RxJavaPlugins.setNewThreadSchedulerHandler(scheduler -> asynchronousScheduler);

        observeEvent(PluginDisableEvent.class)
                .subscribeOn(Schedulers.newThread())
                .subscribe(pluginDisableEvent -> compositeDisposable.dispose());
    }

    @Override
    public <EventType extends Event> Observable<EventType> observeEvent(Class<EventType> eventClazz) {
        return observeEvent(eventClazz, EventPriority.NORMAL);
    }

    @Override
    public <EventType extends Event> Observable<EventType> observeEvent(Class<EventType> eventClazz, EventPriority eventPriority) {
        return observeEvent(eventClazz, eventPriority, false);
    }

    @Override
    public <EventType extends Event> Observable<EventType> observeEvent(Class<EventType> eventClazz, boolean ignoreCancelled) {
        return observeEvent(eventClazz, EventPriority.NORMAL, ignoreCancelled);
    }

    @Override
    public <EventType extends Event> Observable<EventType> observeEvent(Class<EventType> eventClazz, EventPriority eventPriority, boolean ignoreCancelled) {
        return observeEvent(new EventEmitter<EventType>(eventClazz, ignoreCancelled, eventPriority));
    }

    /**
     * Create an observable based on the given emitter.
     *
     * @param eventEmitter The emitter.
     * @param <EventType>  The type of the event.
     * @return The observable.
     */
    private <EventType extends Event> Observable<EventType> observeEvent(EventEmitter<EventType> eventEmitter) {
        return Observable.create(eventEmitter)
                .doOnSubscribe(compositeDisposable::add)
                .doOnDispose(() -> HandlerList.unregisterAll(eventEmitter.getListener()));
    }

    @Override
    public Observable<CommandExecution> observeCommand(String command) {
        return observeCommand(command, "Just another random command");
    }

    @Override
    public Observable<CommandExecution> observeCommand(String command, String description) {
        return observeCommand(command, description, "/" + command);
    }

    @Override
    public Observable<CommandExecution> observeCommand(String command, String description, String usageMessage) {
        return observeCommand(command, description, usageMessage, Lists.newArrayList());
    }

    @Override
    public Observable<CommandExecution> observeCommand(String command, String description, String usageMessage, String... aliases) {
        return observeCommand(command, description, usageMessage, (ArrayList<String>) Arrays.asList(aliases));
    }

    @Override
    public Observable<CommandExecution> observeCommand(String command, String description, String usageMessage, ArrayList<String> aliases) {
        final Command[] bukkitCommand = {null};

        return Observable.create(observableEmitter -> {
            bukkitCommand[0] = new BukkitCommand(command, description, usageMessage, aliases) {
                @Override
                public boolean execute(CommandSender commandSender, String label, String[] args) {
                    observableEmitter.onNext(new CommandExecution(commandSender, bukkitCommand[0], label, args));
                    return true;
                }
            };
            commandService.addCommand(bukkitCommand[0]);
        });
    }

    @Override
    public Observable<PacketEvent> observePacket(PacketType packetType) {
        return observePacket(packetType, ConnectionSide.SERVER_SIDE);
    }

    @Override
    public Observable<PacketEvent> observePacket(PacketType packetType, ConnectionSide connectionSide) {
        final PacketAdapter[] packetAdapter = new PacketAdapter[1];

        PacketAdapter.AdapterParameteters adapterParameteters = new PacketAdapter.AdapterParameteters();
        adapterParameteters.connectionSide(connectionSide);
        adapterParameteters.plugin(plugin);
        adapterParameteters.types(packetType);

        return Observable.create((ObservableEmitter<PacketEvent> observableEmitter) -> {
            packetAdapter[0] = new PacketAdapter(adapterParameteters) {
                @Override
                public void onPacketReceiving(PacketEvent event) {
                    observableEmitter.onNext(event);
                }

                @Override
                public void onPacketSending(PacketEvent event) {
                    observableEmitter.onNext(event);
                }
            };

            ProtocolLibrary.getProtocolManager().addPacketListener(packetAdapter[0]);
        }).doOnDispose(() -> ProtocolLibrary.getProtocolManager().removePacketListener(packetAdapter[0]));
    }

    /**
     * A custom event emitter that will emit bukkit events into observables.
     *
     * @param <EventType> The tyoe of the event.
     */
    private class EventEmitter<EventType> implements ObservableOnSubscribe<EventType> {

        /**
         * The bukkit skeleton listener.
         */
        private final Listener listener = new Listener() {
        };

        /**
         * The clazz of the event.
         */
        private final Class<? extends Event> eventClazz;

        /**
         * if the listener should ignore cancelled events.
         */
        private final boolean ignoreCancelled;

        /**
         * The priority of the listener.
         */
        private final EventPriority eventPriority;

        /**
         * Create a new event emitter.
         *
         * @param eventClazz      The class of the event..
         * @param ignoreCancelled If cancelled events should be ignored.
         * @param eventPriority   The event priority.
         */
        EventEmitter(Class<? extends Event> eventClazz, boolean ignoreCancelled, EventPriority eventPriority) {
            this.eventClazz = eventClazz;
            this.ignoreCancelled = ignoreCancelled;
            this.eventPriority = eventPriority;
        }

        @Override
        public void subscribe(ObservableEmitter<EventType> observableEmitter) {
            pluginManager.registerEvent(eventClazz, listener, eventPriority, (listener1, event) -> {
                if (eventClazz.isAssignableFrom(event.getClass())) {
                    observableEmitter.onNext((EventType) event);
                }
            }, plugin, ignoreCancelled);
        }

        /**
         * Get the skeleton listener.
         *
         * @return The listener.
         */
        Listener getListener() {
            return listener;
        }
    }
}
