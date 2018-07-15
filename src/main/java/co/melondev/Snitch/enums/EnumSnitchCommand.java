package co.melondev.Snitch.enums;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.entities.*;
import co.melondev.Snitch.util.MsgUtil;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Devon on 7/13/18.
 */
public enum EnumSnitchCommand {

    ACTIONS(Arrays.asList("actions", "a"), "", "View list of actions", "snitch.actions") {
        @Override
        public void run(CommandSender sender, List<String> args) throws SQLException {
            List<String> generals = new ArrayList<>();
            List<String> fullNames = new ArrayList<>();
            for (EnumAction action : EnumAction.values()) {
                if (!generals.contains(action.getName())) {
                    generals.add(action.getName());
                }
                fullNames.add(action.name());
            }
            sender.sendMessage(MsgUtil.info("Quick Reference: §f" + String.join(", ", generals)));
            sender.sendMessage(MsgUtil.info("Specific: §f" + String.join(", ", fullNames)));
        }
    },
    PARAMS(Arrays.asList("params", "p"), "", "View list of params", "snitch.params") {
        @Override
        public void run(CommandSender sender, List<String> args) throws SQLException {
            sender.sendMessage(MsgUtil.info("Params"));
            StringBuilder lookupExample = new StringBuilder();
            StringBuilder rollbackExample = new StringBuilder();
            Random r = new Random();
            for (EnumParam param : EnumParam.values()) {
                sender.sendMessage(MsgUtil.record(String.join(", ", param.getKeywords())) + " §8 - §6" + param.getExample());
                if (r.nextBoolean()) {
                    lookupExample.append(param.getExample()).append(" ");
                }
                if (r.nextBoolean()) {
                    rollbackExample.append(param.getExample()).append(" ");
                }
            }
            sender.sendMessage(MsgUtil.info("Lookup Example: §6/snitch l " + lookupExample.toString()));
            sender.sendMessage(MsgUtil.info("Rollback Example: §6/snitch rb " + rollbackExample.toString()));
        }
    },
    ROLLBACK(Arrays.asList("rollback", "rb"), "<params>", "Perform a rollback", "snitch.rollback") {
        @Override
        public void run(CommandSender sender, List<String> args) throws SQLException {
            if ((sender instanceof Player)) {
                Player player = (Player) sender;

                SnitchQuery query = new SnitchQuery();
                query.parseParams(player, args);
                List<SnitchEntry> entryList = SnitchPlugin.getInstance().getStorage().performLookup(query);
                SnitchSession session = EnumSnitchCommand.getOrCreateSession(player, query, entryList, 1);

                final long startTime = System.currentTimeMillis();
                SnitchRollback rollback = new SnitchRollback(session, (player1, result) -> {
                    long diff = System.currentTimeMillis() - startTime;
                    player1.sendMessage(MsgUtil.success("Rollback successfully completed in " + diff + "ms."));
                    player1.sendMessage(MsgUtil.record("Total Changes: " + result.getApplied() + "§c§o (" + result.getFailed() + " Failed)"));
                    player1.sendMessage(MsgUtil.record("If you made a mistake, you can §e/snitch restore <param>§7."));
                });
                rollback.apply();

            } else {
                sender.sendMessage(MsgUtil.error("You must be a player to use the preview command."));
            }
        }
    },
    RESTORE(Arrays.asList("restore", "rs"), "<params>", "Restore changes from a rollback", "snitch.restore") {
        @Override
        public void run(CommandSender sender, List<String> args) throws SQLException {
            if ((sender instanceof Player)) {
                Player player = (Player) sender;

                SnitchQuery query = new SnitchQuery();
                query.parseParams(player, args);
                List<SnitchEntry> entryList = SnitchPlugin.getInstance().getStorage().performLookup(query);
                SnitchSession session = EnumSnitchCommand.getOrCreateSession(player, query, entryList, 1);

                final long startTime = System.currentTimeMillis();
                SnitchRestore restore = new SnitchRestore(session, (player1, result) -> {
                    long diff = System.currentTimeMillis() - startTime;
                    player1.sendMessage(MsgUtil.success("Restore successfully completed in " + diff + "ms."));
                    player1.sendMessage(MsgUtil.record("Total Changes: " + result.getApplied() + "§c§o (" + result.getFailed() + " Failed)"));
                });
                restore.apply();

            } else {
                sender.sendMessage(MsgUtil.error("You must be a player to use the preview command."));
            }
        }
    },
    PREVIEW(Arrays.asList("preview", "pv"), "<params>", "Perform a rollback preview", "snitch.preview") {
        @Override
        public void run(CommandSender sender, List<String> args) throws SQLException {
            if ((sender instanceof Player)) {
                Player player = (Player) sender;
                if (args.size() == 1) {
                    String cmd = args.get(0);
                    if (cmd.equalsIgnoreCase("apply")) {
                        SnitchSession session = SnitchPlugin.getInstance().getPlayerManager().getSession(player);
                        if (session != null && session.getActivePreview() != null) {
                            session.getActivePreview().applyPreview();
                            session.setActivePreview(null);
                        } else {
                            sender.sendMessage(MsgUtil.error("You don't have any active preview! Get started with \"/snitch pv <params>\"."));
                            return;
                        }
                    } else if (cmd.equalsIgnoreCase("cancel")) {
                        SnitchSession session = SnitchPlugin.getInstance().getPlayerManager().getSession(player);
                        if (session != null && session.getActivePreview() != null) {
                            session.getActivePreview().cancelPreview();
                            session.setActivePreview(null);
                        } else {
                            sender.sendMessage(MsgUtil.error("You don't have any active preview! Get started with \"/snitch pv <params>\"."));
                            return;
                        }
                    }
                }

                SnitchQuery query = new SnitchQuery();
                query.parseParams(player, args);
                List<SnitchEntry> entryList = SnitchPlugin.getInstance().getStorage().performLookup(query);
                SnitchSession session = EnumSnitchCommand.getOrCreateSession(player, query, entryList, 1);
                if (session.getActivePreview() != null) {
                    session.getActivePreview().cancelPreview();
                    session.setActivePreview(null);
                }

                SnitchPreview preview = new SnitchPreview(session, (player1, result) -> {
                    player.sendMessage(MsgUtil.success("Previewing rollback for " + query.getSearchSummary().toLowerCase()));
                    player.sendMessage(MsgUtil.record("Showing " + result.getApplied() + " planned changes"));
                    player.sendMessage(MsgUtil.record("Type §a/snitch pv apply§7 or §c/snitch pv cancel§7 to continue."));
                });
                preview.apply();

            } else {
                sender.sendMessage(MsgUtil.error("You must be a player to use the preview command."));
            }
        }
    },
    LOOKUP(Arrays.asList("lookup", "l"), "<params>", "Perform a lookup", "snitch.lookup") {
        @Override
        public void run(CommandSender sender, List<String> args) throws SQLException {
            if ((sender instanceof Player)) {

                Player player = (Player) sender;
                SnitchQuery query = new SnitchQuery();
                query.parseParams(player, args);
                List<SnitchEntry> entryList = SnitchPlugin.getInstance().getStorage().performLookup(query);
                EnumSnitchCommand.getOrCreateSession(player, query, entryList, 1);
                MsgUtil.sendRecords(sender, query, entryList, 1, 7);

            } else {
                sender.sendMessage(MsgUtil.error("You must be a player to use the lookup command."));
            }
        }
    },
    NEAR(Arrays.asList("near"), "[range]", "Perform a quick area lookup", "snitch.near") {
        @Override
        public void run(CommandSender sender, List<String> args) throws SQLException {
            if ((sender instanceof Player)) {

                Player player = (Player) sender;

                int range = 5;
                if (args.size() == 2) {
                    try {
                        range = Integer.parseInt(args.get(1));
                        if (range < 1) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException ex) {
                        sender.sendMessage("§cInvalid number: " + range);
                    }
                }

                SnitchQuery query = new SnitchQuery().range(5).relativeTo(new SnitchPosition(player.getLocation()));
                query.analyzePermissions(player);
                List<SnitchEntry> entryList = SnitchPlugin.getInstance().getStorage().performLookup(query);
                EnumSnitchCommand.getOrCreateSession(player, query, entryList, 1);
                MsgUtil.sendRecords(sender, query, entryList, 1, 7);

            } else {
                sender.sendMessage(MsgUtil.error("You must be a player to use the near command."));
            }
        }
    },
    INSPECTOR(Arrays.asList("inspect", "i"), "", "Toggle the inspector", "snitch.inspector") {
        @Override
        public void run(CommandSender sender, List<String> args) {
            if ((sender instanceof Player)) {

                Player player = (Player) sender;
                if (player.hasMetadata("snitch-inspector")) {
                    player.removeMetadata("snitch-inspector", SnitchPlugin.getInstance());
                    sender.sendMessage(MsgUtil.success("Turned off the inspector."));
                } else {
                    player.setMetadata("snitch-inspector", new FixedMetadataValue(SnitchPlugin.getInstance(), null));
                    sender.sendMessage(MsgUtil.success("Turned on the inspector."));
                    sender.sendMessage(MsgUtil.info("Left-Click a block to investigate it."));
                    sender.sendMessage(MsgUtil.info("Right-Click a block to investigate the space adjacent to it."));
                }

            } else {
                sender.sendMessage(MsgUtil.error("You must be a player to use the inspector."));
            }
        }
    },
    NEXT(Arrays.asList("next"), "", "Go to next page", "snitch.lookup") {
        @Override
        public void run(CommandSender sender, List<String> args) throws SQLException {
            if ((sender instanceof Player)) {
                Player player = (Player) sender;
                SnitchSession session = SnitchPlugin.getInstance().getPlayerManager().getSession(player);
                if (session != null && !session.getEntries().isEmpty()) {

                    session.setPage(session.getPage() + 1);
                    MsgUtil.sendRecords(sender, session.getQuery(), session.getEntries(), session.getPage(), 7);

                } else {
                    sender.sendMessage(MsgUtil.error("No results to view the next page of. Try a lookup command first."));
                }
            } else {
                sender.sendMessage(MsgUtil.error("You must be a player to use the pagination commands."));
            }
        }
    },
    PREVIOUS(Arrays.asList("prev"), "", "Go to previous page", "snitch.lookup") {
        @Override
        public void run(CommandSender sender, List<String> args) throws SQLException {
            if ((sender instanceof Player)) {
                Player player = (Player) sender;
                SnitchSession session = SnitchPlugin.getInstance().getPlayerManager().getSession(player);
                if (session != null && !session.getEntries().isEmpty()) {

                    if (session.getPage() == 1) {
                        sender.sendMessage(MsgUtil.error("Already on the first page!"));
                        return;
                    }

                    session.setPage(session.getPage() - 1);
                    MsgUtil.sendRecords(sender, session.getQuery(), session.getEntries(), session.getPage(), 7);

                } else {
                    sender.sendMessage(MsgUtil.error("No results to view the previous page of. Try a lookup command first."));
                }
            } else {
                sender.sendMessage(MsgUtil.error("You must be a player to use the pagination commands."));
            }
        }
    },
    PAGE(Arrays.asList("page", "pv"), "<page>", "Go to specific page", "snitch.lookup") {
        @Override
        public void run(CommandSender sender, List<String> args) throws SQLException {
            if ((sender instanceof Player)) {
                Player player = (Player) sender;
                Validate.isTrue(args.size() == 1, "Specify page number.");
                Validate.isTrue(NumberUtils.isNumber(args.get(0)), "Specify valid number.");
                int page = Integer.parseInt(args.get(0));
                Validate.isTrue(page >= 1, "Page must be at least 1.");
                SnitchSession session = SnitchPlugin.getInstance().getPlayerManager().getSession(player);
                if (session != null && !session.getEntries().isEmpty()) {
                    session.setPage(page);
                    MsgUtil.sendRecords(sender, session.getQuery(), session.getEntries(), session.getPage(), 7);
                } else {
                    sender.sendMessage(MsgUtil.error("No results to view pages of. Try a lookup command first."));
                }
            } else {
                sender.sendMessage(MsgUtil.error("You must be a player to use the pagination commands."));
            }
        }
    };

    private List<String> commands;
    private String arguments;
    private String description;
    private String permission;

    EnumSnitchCommand(List<String> commands, String arguments, String description, String permission) {
        this.commands = commands;
        this.arguments = arguments;
        this.description = description;
        this.permission = permission;
    }

    private static SnitchSession getOrCreateSession(Player player, SnitchQuery query, List<SnitchEntry> entries, int page) {
        SnitchSession session = SnitchPlugin.getInstance().getPlayerManager().getSession(player);
        if (session == null) {
            session = new SnitchSession(player, query, entries, page);
            SnitchPlugin.getInstance().getPlayerManager().setSession(player, session);
        }
        session.setQuery(query);
        session.setEntries(entries);
        session.setPage(page);
        return session;
    }

    public static EnumSnitchCommand getByCommand(String label) {
        for (EnumSnitchCommand cmd : values()) {
            if (cmd.getCommands().contains(label.toLowerCase())) {
                return cmd;
            }
        }
        return null;
    }

    public abstract void run(CommandSender sender, List<String> args) throws SQLException;

    public List<String> getCommands() {
        return commands;
    }

    public String getArguments() {
        return arguments;
    }

    public String getDescription() {
        return description;
    }

    public String getPermission() {
        return permission;
    }
}
