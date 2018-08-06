package co.melondev.Snitch.enums;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.entities.*;
import co.melondev.Snitch.util.AdjustedBlock;
import co.melondev.Snitch.util.BlockUtil;
import co.melondev.Snitch.util.MsgUtil;
import co.melondev.Snitch.util.SnitchDatabaseException;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
 *
 * Handles command logic for Snitch
 */
public enum EnumSnitchCommand {

    /**
     * Provides an in-game reference for available actions
     */
    ACTIONS(Arrays.asList("actions", "a"), "", "View list of actions", "snitch.actions") {
        @Override
        public void run(CommandSender sender, List<String> args) throws SnitchDatabaseException {
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
    /**
     * Provides an in-game reference for available params with examples
     */
    PARAMS(Arrays.asList("params", "p"), "", "View list of params", "snitch.params") {
        @Override
        public void run(CommandSender sender, List<String> args) throws SnitchDatabaseException {
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
    /**
     * Runs a {@link SnitchRollback} against the provided {@link SnitchQuery}
     */
    ROLLBACK(Arrays.asList("rollback", "rb"), "<params>", "Perform a rollback", "snitch.rollback") {
        @Override
        public void run(CommandSender sender, List<String> args) throws SnitchDatabaseException {
            if ((sender instanceof Player)) {
                Player player = (Player) sender;

                SnitchQuery query = new SnitchQuery();
                query.parseParams(player, args);
                List<SnitchEntry> entryList = SnitchPlugin.getInstance().getStorage().performLookup(query);
                SnitchSession session = EnumSnitchCommand.getOrCreateSession(player, query, entryList, 1);

                final long startTime = System.currentTimeMillis();
                SnitchRollback rollback = new SnitchRollback(session, new SnitchRollback.DefaultRollbackCallback(startTime));
                rollback.apply();

            } else {
                sender.sendMessage(MsgUtil.error("You must be a player to use the preview command."));
            }
        }
    },
    /**
     * Runs a {@link SnitchRestore} using the provided {@link SnitchQuery}
     */
    RESTORE(Arrays.asList("restore", "rs"), "<params>", "Restore changes from a rollback", "snitch.restore") {
        @Override
        public void run(CommandSender sender, List<String> args) throws SnitchDatabaseException {
            if ((sender instanceof Player)) {
                Player player = (Player) sender;

                SnitchQuery query = new SnitchQuery();
                query.parseParams(player, args);
                List<SnitchEntry> entryList = SnitchPlugin.getInstance().getStorage().performLookup(query);
                SnitchSession session = EnumSnitchCommand.getOrCreateSession(player, query, entryList, 1);

                final long startTime = System.currentTimeMillis();
                SnitchRestore restore = new SnitchRestore(session, new SnitchRestore.DefaultRestoreCallback(startTime));
                restore.apply();

            } else {
                sender.sendMessage(MsgUtil.error("You must be a player to use the preview command."));
            }
        }
    },
    /**
     * Runs a {@link SnitchPreview} using the provided {@link SnitchQuery}
     */
    PREVIEW(Arrays.asList("preview", "pv"), "<params>", "Perform a rollback preview", "snitch.preview") {
        @Override
        public void run(CommandSender sender, List<String> args) throws SnitchDatabaseException {
            if ((sender instanceof Player)) {
                Player player = (Player) sender;
                if (args.size() == 1) {
                    String cmd = args.get(0);
                    if (cmd.equalsIgnoreCase("apply")) {
                        SnitchSession session = SnitchPlugin.getInstance().getPlayerManager().getSession(player);
                        if (session != null && session.getActivePreview() != null) {
                            session.getActivePreview().applyPreview();
                            session.setActivePreview(null);
                            return;
                        } else {
                            sender.sendMessage(MsgUtil.error("You don't have any active preview! Get started with \"/snitch pv <params>\"."));
                            return;
                        }
                    } else if (cmd.equalsIgnoreCase("cancel")) {
                        SnitchSession session = SnitchPlugin.getInstance().getPlayerManager().getSession(player);
                        if (session != null && session.getActivePreview() != null) {
                            session.getActivePreview().cancelPreview();
                            session.setActivePreview(null);
                            return;
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
                SnitchPreview preview = new SnitchPreview(session, new SnitchPreview.DefaultPreviewCallback(query));
                preview.apply();

            } else {
                sender.sendMessage(MsgUtil.error("You must be a player to use the preview command."));
            }
        }
    },
    /**
     * Lookup records using the provided {@link SnitchQuery}
     */
    LOOKUP(Arrays.asList("lookup", "l"), "<params>", "Perform a lookup", "snitch.lookup") {
        @Override
        public void run(CommandSender sender, List<String> args) throws SnitchDatabaseException {
            if ((sender instanceof Player)) {

                Player player = (Player) sender;
                SnitchQuery query = new SnitchQuery();
                query.parseParams(player, args);
                if (!query.hasLimit()) {
                    query.limit(1000);
                }
                List<SnitchEntry> entryList = SnitchPlugin.getInstance().getStorage().performLookup(query);
                EnumSnitchCommand.getOrCreateSession(player, query, entryList, 1);
                MsgUtil.sendRecords(sender, query, entryList, 1, 7);

            } else {
                sender.sendMessage(MsgUtil.error("You must be a player to use the lookup command."));
            }
        }
    },
    /**
     * Performs a {@link EnumParam#RADIUS} lookup quickly
     */
    NEAR(Arrays.asList("near"), "[range]", "Perform a quick area lookup", "snitch.near") {
        @Override
        public void run(CommandSender sender, List<String> args) throws SnitchDatabaseException {
            if ((sender instanceof Player)) {

                Player player = (Player) sender;

                int range = 5;
                if (args.size() == 1) {
                    try {
                        range = Integer.parseInt(args.get(0));
                        if (range < 1) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException ex) {
                        sender.sendMessage("§cInvalid number: " + range);
                    }
                }

                SnitchQuery query = new SnitchQuery().relativeTo(new SnitchPosition(player.getLocation())).inWorld(player.getWorld()).range(range);
                query.analyzePermissions(player);
                List<SnitchEntry> entryList = SnitchPlugin.getInstance().getStorage().performLookup(query);
                EnumSnitchCommand.getOrCreateSession(player, query, entryList, 1);
                MsgUtil.sendRecords(sender, query, entryList, 1, 7);

            } else {
                sender.sendMessage(MsgUtil.error("You must be a player to use the near command."));
            }
        }
    },
    /**
     * Teleports to a {@link SnitchEntry} from within a {@link SnitchSession}
     */
    TELEPORT(Arrays.asList("teleport", "tp"), "<#>", "Teleport to a log entry", "snitch.teleport") {
        @Override
        public void run(CommandSender sender, List<String> args) throws SnitchDatabaseException {
            if ((sender instanceof Player)) {

                if (args.size() == 1) {
                    try {

                        int id = Integer.parseInt(args.get(0));

                        SnitchSession session = SnitchPlugin.getInstance().getPlayerManager().getSession((Player) sender);
                        if (session != null && !session.getEntries().isEmpty()) {
                            if (id < session.getEntries().size() && id >= 0) {
                                SnitchEntry entry = session.getEntries().get(id);
                                Location loc = entry.getSnitchPosition().toLocation(entry.getSnitchWorld().getBukkitWorld());
                                ((Player) sender).teleport(loc);
                                sender.sendMessage(MsgUtil.success("Teleported to entry #" + id + ": §7" + entry.getDescriptor(sender)));
                            } else {
                                sender.sendMessage(MsgUtil.error("No entry found matching that ID."));
                            }
                        } else {
                            sender.sendMessage(MsgUtil.error("No results to teleport to. Try a lookup command first."));
                        }

                    } catch (NumberFormatException ex) {
                        sender.sendMessage(MsgUtil.error("Specify a valid entry ID."));
                    }
                } else {
                    sender.sendMessage(MsgUtil.error("You need to specify an entry ID."));
                }

            } else {
                sender.sendMessage(MsgUtil.error("You must be a player to use the teleport command."));
            }
        }
    },
    /**
     * Toggle the inspector - allowing lookup by clicking blocks
     */
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
    /**
     * Navigate to the next page in a {@link SnitchSession}
     */
    NEXT(Arrays.asList("next"), "", "Go to next page", "snitch.lookup") {
        @Override
        public void run(CommandSender sender, List<String> args) throws SnitchDatabaseException {
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
    /**
     * Navigate to the previous page in a {@link SnitchSession}
     */
    PREVIOUS(Arrays.asList("prev"), "", "Go to previous page", "snitch.lookup") {
        @Override
        public void run(CommandSender sender, List<String> args) throws SnitchDatabaseException {
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
    /**
     * Navigate to a specific page within a {@link SnitchSession}
     */
    PAGE(Arrays.asList("page", "pg"), "<page>", "Go to specific page", "snitch.lookup") {
        @Override
        public void run(CommandSender sender, List<String> args) throws SnitchDatabaseException {
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
    },
    /**
     * Drain surrounding liquids (water, lava) in the provided radius
     */
    DRAIN(Arrays.asList("drain", "dr"), "[radius]", "Drain liquids", "snitch.drain") {
        @Override
        public void run(CommandSender sender, List<String> args) throws SnitchDatabaseException {
            if ((sender instanceof Player)) {

                Player player = (Player) sender;

                int range = 10;
                if (args.size() == 1) {
                    try {
                        range = Integer.parseInt(args.get(0));
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(MsgUtil.error("You must provide a valid radius."));
                    }
                }

                int finalRange = range;
                Bukkit.getServer().getScheduler().runTask(SnitchPlugin.getInstance(), () -> {
                    List<AdjustedBlock> changed = BlockUtil.removeNear(Arrays.asList(Material.WATER, Material.STATIONARY_WATER, Material.LAVA, Material.STATIONARY_LAVA), player.getLocation(), finalRange);
                    if (changed.isEmpty()) {
                        sender.sendMessage(MsgUtil.error("There were no liquids to drain withn " + finalRange + " blocks. You can try a larger radius with \"/snitch dr <radius>\"."));
                    } else {
                        sender.sendMessage(MsgUtil.success("Drained " + changed.size() + " liquids within " + finalRange + " blocks."));
                    }
                });

            } else {
                sender.sendMessage(MsgUtil.error("You must be a player to use the extinguish command."));
            }
        }
    },
    /**
     * Undo the last activity performed in a {@link SnitchSession}
     */
    UNDO(Arrays.asList("undo", "un"), "", "Undo last rollback or restore", "snitch.undo") {
        @Override
        public void run(CommandSender sender, List<String> args) throws SnitchDatabaseException {
            if ((sender instanceof Player)) {

                Player player = (Player) sender;

                SnitchSession session = SnitchPlugin.getInstance().getPlayerManager().getSession(player);
                if (session != null) {

                    if (session.getLastActivity() != null && (session.getLastActivity() == EnumSnitchActivity.ROLLBACK) ||
                            (session.getLastActivity() == EnumSnitchActivity.RESTORE)) {

                        if (session.getLastActivity() == EnumSnitchActivity.ROLLBACK) {
                            SnitchRestore restore = new SnitchRestore(session, new SnitchRestore.DefaultRestoreCallback(System.currentTimeMillis()));
                            restore.apply();
                        } else if (session.getLastActivity() == EnumSnitchActivity.RESTORE) {
                            SnitchRollback rollback = new SnitchRollback(session, new SnitchRollback.DefaultRollbackCallback(System.currentTimeMillis()));
                            rollback.apply();
                        } else {
                            sender.sendMessage(MsgUtil.error("Not sure how to handle undo for: " + session.getLastActivity().name()));
                        }

                    } else {
                        sender.sendMessage(MsgUtil.error("Your last activity can't be undone! Try this command after performing either a rollback or a restore."));
                    }

                } else {
                    sender.sendMessage(MsgUtil.error("No recent action to undo!"));
                }

            } else {
                sender.sendMessage(MsgUtil.error("You must be a player to use the undo command."));
            }
        }
    },
    /**
     * Removes fire within the provided radius
     */
    EXTINGUISH(Arrays.asList("extinguish", "ex"), "[radius]", "Extinguish fires", "snitch.extinguish") {
        @Override
        public void run(CommandSender sender, List<String> args) throws SnitchDatabaseException {
            if ((sender instanceof Player)) {

                Player player = (Player) sender;

                int range = 10;
                if (args.size() == 1) {
                    try {
                        range = Integer.parseInt(args.get(0));
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(MsgUtil.error("You must provide a valid radius."));
                    }
                }

                int finalRange = range;
                Bukkit.getServer().getScheduler().runTask(SnitchPlugin.getInstance(), () -> {
                    List<AdjustedBlock> changed = BlockUtil.removeNear(Arrays.asList(Material.FIRE), player.getLocation(), finalRange);
                    if (changed.isEmpty()) {
                        sender.sendMessage(MsgUtil.error("There were no fires to extinguish withn " + finalRange + " blocks. You can try a larger radius with \"/snitch ex <radius>\"."));
                    } else {
                        sender.sendMessage(MsgUtil.success("Extinguished " + changed.size() + " fires within " + finalRange + " blocks."));
                    }
                });

            } else {
                sender.sendMessage(MsgUtil.error("You must be a player to use the extinguish command."));
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

    /**
     * Gets a player session, or create one if it doesn't exist
     *
     * @param player  the player using this session
     * @param query   the query being used
     * @param entries the entries that were returned by this query
     * @param page    the page
     * @return the session
     */
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

    /**
     * Get a command with the matching label or alias
     * @param label     the command label
     * @return the matching command
     */
    public static EnumSnitchCommand getByCommand(String label) {
        for (EnumSnitchCommand cmd : values()) {
            if (cmd.getCommands().contains(label.toLowerCase())) {
                return cmd;
            }
        }
        return null;
    }

    /**
     * Handles command logic
     * @param sender        the command sender
     * @param args          the arguments of the command
     * @throws SQLException if there's any database errors
     */
    public abstract void run(CommandSender sender, List<String> args) throws SnitchDatabaseException;

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
