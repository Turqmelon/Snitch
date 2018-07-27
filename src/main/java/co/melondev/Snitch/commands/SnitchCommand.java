package co.melondev.Snitch.commands;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.enums.EnumSnitchCommand;
import co.melondev.Snitch.util.MsgUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Devon on 7/13/18.
 *
 * The primary command for Snitch. We'll pass these params to {@link EnumSnitchCommand} for processing
 */
public class SnitchCommand implements CommandExecutor {


    /**
     * The primary instance of Snitch.
     */
    private SnitchPlugin i;

    public SnitchCommand(SnitchPlugin i) {
        this.i = i;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        // A majority of our commands operate in another thread. For world-modification specific ones, we can send it back to the main thread when needed.
        i.async(() -> {
            try {
                // #shamelessplug
                if (args.length == 0) {
                    sender.sendMessage(MsgUtil.info(i.getDescription().getName() + " v" + i.getDescription().getVersion()));
                    sender.sendMessage(MsgUtil.info("Developed by Melon Development, Inc."));
                    sender.sendMessage(MsgUtil.info("Help: §6/snitch help"));
                    return;
                } else {
                    String cmd = args[0];
                    // Display a list of the commands for the plugin. These are from EnumSnitchCommand.
                    if (cmd.equalsIgnoreCase("help") || cmd.equalsIgnoreCase("?")) {
                        sender.sendMessage(MsgUtil.info("Commands"));
                        boolean permsForAny = false;
                        for (EnumSnitchCommand c : EnumSnitchCommand.values()) {
                            if (c.getPermission() == null || sender.hasPermission(c.getPermission())) {
                                permsForAny = true;
                                String usage = c.getArguments().length() > 0 ? " " + c.getArguments() + " " : " ";
                                sender.sendMessage(MsgUtil.record("/snitch " + String.join("|", c.getCommands())) + usage + "§o" + c.getDescription());
                            }
                        }
                        // If the user doesn't have permission for any Snitch commands, tell them so.
                        if (!permsForAny) {
                            sender.sendMessage(MsgUtil.error("You don't have access to any Snitch commands."));
                        }
                    } else {

                        // Match the first argument to a Snitch subcommand.
                        EnumSnitchCommand c = EnumSnitchCommand.getByCommand(cmd);
                        if (c != null) {

                            // Double check that they have permission, if a permission is specified
                            if (c.getPermission() == null || sender.hasPermission(c.getPermission())) {

                                List<String> a = new ArrayList<>();
                                a.addAll(Arrays.asList(args).subList(1, args.length));

                                // onwar dto processing!
                                c.run(sender, a);

                            } else {
                                sender.sendMessage(MsgUtil.error("You don't have permission to " + c.getDescription().toLowerCase() + "."));
                            }

                        } else {
                            sender.sendMessage(MsgUtil.error("Unknown command: /snitch " + cmd));
                        }
                    }
                }

                // Default error handling. Any command errors will through an IAE.
            } catch (IllegalArgumentException ex) {
                sender.sendMessage(MsgUtil.error(ex.getMessage()));
            } catch (SQLException e) {
                // Database errors. We don't want to display the problem publicly so we instead log it to console.
                sender.sendMessage(MsgUtil.error("Internal database error. Check console for details."));
                e.printStackTrace();
            }
        });

        return true;
    }
}
