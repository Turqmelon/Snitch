package co.melondev.Snitch.enums;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.entities.SnitchPlayer;
import co.melondev.Snitch.entities.SnitchPosition;
import co.melondev.Snitch.entities.SnitchQuery;
import co.melondev.Snitch.entities.SnitchWorld;
import co.melondev.Snitch.util.SnitchDatabaseException;
import co.melondev.Snitch.util.TimeUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Devon on 7/13/18.
 *
 * Stores all parameters that can be searched by, as well as the logic for interpreting queries
 */
@SuppressWarnings("Duplicates")
public enum EnumParam {

    /**
     * Allows searching for records by a specific {@link SnitchPlayer}
     */
    PLAYER(Arrays.asList("player", "players", "p", "actor"), "player Turqmelon") {
        @Override
        public void parse(Player player, SnitchQuery query, String[] values) throws SnitchDatabaseException {
            for (String playerName : values) {
                boolean exclude = playerName.startsWith("!");
                if (exclude) {
                    playerName = playerName.substring(1);
                }
                SnitchPlayer pl = SnitchPlugin.getInstance().getStorage().getPlayer(playerName);
                Validate.notNull(pl, "Unknown player: " + playerName);
                if (exclude) {
                    query.addExcludedPlayer(pl);
                } else {
                    query.addPlayers(pl);
                }
            }
        }
    },
    /**
     * Allows searching for records by specific {@link EnumAction}
     */
    ACTION(Arrays.asList("action", "actions", "a"), "action break") {
        @Override
        public void parse(Player player, SnitchQuery query, String[] values) throws SnitchDatabaseException {
            for (String action : values) {
                boolean exclude = action.startsWith("!");
                if (exclude) {
                    action = action.substring(1);
                }
                List<EnumAction> results = EnumAction.getByName(action);
                Validate.notEmpty(results, "Unknown action: " + action);
                for (EnumAction a : results) {
                    if (exclude) {
                        query.addExcludedAction(a);
                    } else {
                        query.addActions(a);
                    }
                }
            }
        }
    },
    /**
     * Allows filtering results to only those that happened from a specific time
     */
    SINCE(Arrays.asList("since", "from", "s"), "since 1d") {
        @Override
        public void parse(Player player, SnitchQuery query, String[] values) throws SnitchDatabaseException {
            Validate.notEmpty(values, "No time specified for " + name() + ".");
            Validate.isTrue(values.length == 1, "Multiple time values provided for " + name() + ".");
            query.setSinceTime(EnumParam.getUnixTime(values[0], false));
        }
    },
    /**
     * Allows filtering results to only those that happened before a specific time
     */
    BEFORE(Arrays.asList("before", "prior", "b"), "before 06/12/18") {
        @Override
        public void parse(Player player, SnitchQuery query, String[] values) throws SnitchDatabaseException {
            Validate.notEmpty(values, "No time specified for " + name() + ".");
            Validate.isTrue(values.length == 1, "Multiple time values provided for " + name() + ".");
            query.setBeforeTime(EnumParam.getUnixTime(values[0], false));
        }
    },
    /**
     * Allows filtering results to only those within a specific world
     * TODO: Add the ability to parse the "world" name correctly.
     */
    WORLD(Arrays.asList("world", "w"), "world world_nether") {
        @Override
        public void parse(Player player, SnitchQuery query, String[] values) throws SnitchDatabaseException {
            Validate.notEmpty(values, "No world specified.");
            Validate.isTrue(values.length == 1, "Multiple world values provided.");
            World world = Bukkit.getWorld(values[0]);
            Validate.notNull(world, "Unknown world: " + values[0]);
            SnitchWorld w = SnitchPlugin.getInstance().getStorage().register(world);
            query.setWorld(w);
        }
    },
    /**
     * Allow specifying another coordinate set to search other areas of a world
     * If a world is not specified, we'll default it to the one of the current player
     */
    COORDS(Arrays.asList("coords", "relative", "pos", "position"), "relative 100 150 100") {
        @Override
        public void parse(Player player, SnitchQuery query, String[] values) throws SnitchDatabaseException {
            Validate.isTrue(values.length == 3, "Specify an x, y, and z value.");
            try {
                int x = Integer.parseInt(values[0]);
                int y = Integer.parseInt(values[1]);
                int z = Integer.parseInt(values[2]);
                SnitchPosition pos = new SnitchPosition(x, y, z);
                query.setPosition(pos);
                if (query.getWorld() == null)
                    query.setWorld(SnitchPlugin.getInstance().getStorage().register(player.getWorld()));
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Invalid number provided for " + name() + " param.");
            }
        }
    },
    /**
     * Allows limiting results to a certain set.
     * For rollbacks, we don't add a limit, for lookups we default this to 1,000 if one is not specified.
     */
    LIMIT(Arrays.asList("limit", "lim", "cap"), "limit 50") {
        @Override
        public void parse(Player player, SnitchQuery query, String[] values) throws SnitchDatabaseException {
            Validate.notEmpty(values, "No limit specified.");
            Validate.isTrue(values.length == 1, "Multiple limit values specified.");
            try {
                int limit = Integer.parseInt(values[0]);
                if (limit < 1)
                    throw new NumberFormatException();
                query.limit(limit);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Invalid limit: " + values[0]);
            }
        }
    },
    /**
     * Filters records to only those that happen within a specific range
     */
    RADIUS(Arrays.asList("area", "radius", "range"), "area 20") {
        @Override
        public void parse(Player player, SnitchQuery query, String[] values) throws SnitchDatabaseException {
            Validate.notEmpty(values, "No range specified.");
            Validate.isTrue(values.length == 1, "Multiple range values specified.");
            try {
                int range = Integer.parseInt(values[0]);
                SnitchPosition position = query.getPosition();
                if (position == null) {
                    position = new SnitchPosition(player.getLocation());
                    query.setPosition(position);
                }
                if (query.getWorld() == null) {
                    World world = player.getWorld();
                    SnitchWorld w = SnitchPlugin.getInstance().getStorage().register(world);
                    query.setWorld(w);
                }
                query.setRadius(position, range);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Invalid range: " + values[0]);
            }
        }
    };

    private List<String> keywords;
    private String example;

    EnumParam(List<String> keywords, String example) {
        this.keywords = keywords;
        this.example = example;
    }

    /**
     * Gets the unix time from either an absolute date or a relative time
     *
     * @param date   the date in MM/dd/yy format or in format defined by {@link TimeUtil}
     * @param future whether or not this timestamp is in the future (required by {@link TimeUtil})
     * @return the specified time in unix time
     */
    private static long getUnixTime(String date, boolean future) {
        SimpleDateFormat fullDate = new SimpleDateFormat("MM/dd/yy");
        try {
            return fullDate.parse(date).getTime();
        } catch (ParseException e) {
            try {
                return TimeUtil.parseDateDiff(date, future);
            } catch (Exception e1) {
                throw new IllegalArgumentException("Unknown time or date: " + date);
            }
        }
    }

    /**
     * Matches the specified string to a param
     * @param keyword      the keyword to search for
     * @return the matching param, null if no result
     */
    public static EnumParam getByKeyword(String keyword) {
        for (EnumParam param : values()) {
            if (param.getKeywords().contains(keyword.toLowerCase())) {
                return param;
            }
        }
        return null;
    }

    public String getExample() {
        return example;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    /**
     * Parses the specified values and updates your search query
     * @param player            the player performing the lookup or rollback
     * @param query             the query object associated with their actions
     * @param values            the values provided following the param keyword
     * @throws SQLException     if there's any database errors
     */
    public abstract void parse(Player player, SnitchQuery query, String[] values) throws SnitchDatabaseException;

}
