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
 */
public class SnitchCommand implements CommandExecutor {

    private SnitchPlugin i;

    public SnitchCommand(SnitchPlugin i) {
        this.i = i;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        i.async(() -> {
            try {
                if (args.length == 0) {
                    sender.sendMessage(MsgUtil.info(i.getDescription().getName() + " v" + i.getDescription().getVersion()));
                    sender.sendMessage(MsgUtil.info("Developed by Melon Development, Inc."));
                    sender.sendMessage(MsgUtil.info("Help: §6/snitch help"));
                    return;
                } else {
                    String cmd = args[0];
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
                        if (!permsForAny) {
                            sender.sendMessage(MsgUtil.error("You don't have access to any Snitch commands."));
                        }
                    } else {
                        EnumSnitchCommand c = EnumSnitchCommand.getByCommand(cmd);
                        if (c != null) {

                            if (c.getPermission() == null || sender.hasPermission(c.getPermission())) {

                                List<String> a = new ArrayList<>();
                                a.addAll(Arrays.asList(args).subList(1, args.length));
                                c.run(sender, a);

                            } else {
                                sender.sendMessage(MsgUtil.error("You don't have permission to " + c.getDescription().toLowerCase() + "."));
                            }

                        } else {
                            sender.sendMessage(MsgUtil.error("Unknown command: /snitch " + cmd));
                        }
                    }
                }

            } catch (IllegalArgumentException ex) {
                sender.sendMessage(MsgUtil.error(ex.getMessage()));
            } catch (SQLException e) {
                sender.sendMessage(MsgUtil.error("Internal database error. Check console for details."));
                e.printStackTrace();
            }
        });

        return true;
    }
}
