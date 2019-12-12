package net.ungespielt.lobby.spigot.api.rx;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Represents the execution of a command.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class CommandExecution {

    /**
     * The command sender.
     */
    private final CommandSender commandSender;

    /**
     * The command.
     */
    private final Command command;

    /**
     * THe label.
     */
    private final String label;

    /**
     * The arguments.
     */
    private final String[] args;

    /**
     * Create a new command execution model.
     *
     * @param commandSender The command sender.
     * @param command The command.
     * @param label The label.
     * @param args The arguments.
     */
    public CommandExecution(CommandSender commandSender, Command command, String label, String[] args) {
        this.commandSender = commandSender;
        this.command = command;
        this.label = label;
        this.args = args;
    }

    /**
     * Get the executed command.
     *
     * @return The command.
     */
    public Command getCommand() {
        return command;
    }

    /**
     * Get the dude who sent the command.
     *
     * @return The command sender.
     */
    public CommandSender getCommandSender() {
        return commandSender;
    }

    /**
     * Get the label of the command.
     *
     * @return The label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Get the arguments of the execution.
     *
     * @return The arguments.
     */
    public String[] getArgs() {
        return args;
    }
}
