package net.ungespielt.lobby.spigot.api.rx;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketEvent;
import io.reactivex.Observable;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;

/**
 * The manager for our reactive components.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface RxManager {

    /**
     * Observe on a specific spigot event.
     *
     * @param eventClazz  The class of the event.
     * @param <EventType> The type of the event.
     * @return The observable.
     */
    <EventType extends Event> Observable<EventType> observeEvent(Class<EventType> eventClazz);

    /**
     * Observe on a specific event with a given event priority.
     *
     * @param eventClazz    The class of the event.
     * @param eventPriority The event priority.
     * @param <EventType>   The type of the event.
     * @return The observable.
     */
    <EventType extends Event> Observable<EventType> observeEvent(Class<EventType> eventClazz, EventPriority eventPriority);

    /**
     * Observe on a specific event with a given event priority and ignore cancelled events or not.
     *
     * @param eventClazz      The class of the event.
     * @param eventPriority   The event priority.
     * @param ignoreCancelled If we should ignore cancelled events.
     * @param <EventType>     The type of the event.
     * @return The observable.
     */
    <EventType extends Event> Observable<EventType> observeEvent(Class<EventType> eventClazz, EventPriority eventPriority, boolean ignoreCancelled);

    /**
     * Observe on a specific event and ignore cancelled events or not.
     *
     * @param eventClazz      The class of the event.
     * @param ignoreCancelled If we should ignore cancelled events.
     * @param <EventType>     The type of the event.
     * @return The observable.
     */
    <EventType extends Event> Observable<EventType> observeEvent(Class<EventType> eventClazz, boolean ignoreCancelled);

    /**
     * Observe the given command.
     *
     * @param command The command.
     *
     * @return The observable.
     */
    Observable<CommandExecution> observeCommand(String command);

    /**
     * Observe the given command and give it the given description.
     *
     * @param command The command.
     * @param description The description.
     *
     * @return The observable.
     */
    Observable<CommandExecution> observeCommand(String command, String description);

    /**
     * Observe the given command with the given description and usage hint.
     *
     * @param command The command.
     * @param description The description.
     * @param usageMessage The usage message.
     *
     * @return The observable.
     */
    Observable<CommandExecution> observeCommand(String command, String description, String usageMessage);

    /**
     * Observe the given command with the given description, usage and aliases.
     *
     * @param command The command.
     * @param description The description.
     * @param usageMessage The usage message.
     * @param aliases The aliases.
     *
     * @return The observable.
     */
    Observable<CommandExecution> observeCommand(String command, String description, String usageMessage, String... aliases);

    /**
     * Observe the given command with the given description, usage and aliases.
     *
     * @param command The command.
     * @param description The description.
     * @param usageMessage The usage message.
     * @param aliases The aliases.
     *
     * @return The observable.
     */
    Observable<CommandExecution> observeCommand(String command, String description, String usageMessage, ArrayList<String> aliases);

    /**
     * Observe the given type of packet.
     *
     * @param packetType The type of the packet.
     * @return The observable.
     */
    Observable<PacketEvent> observePacket(PacketType packetType);

    /**
     * Observe tge given type of packet in the given direction.
     *
     * @param packetType     The packet type.
     * @param connectionSide The connection side.
     * @return The observable.
     */
    Observable<PacketEvent> observePacket(PacketType packetType, ConnectionSide connectionSide);
}
