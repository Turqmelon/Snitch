package co.melondev.Snitch.enums;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.entities.SnitchPlayer;
import co.melondev.Snitch.entities.SnitchPosition;
import co.melondev.Snitch.entities.SnitchQuery;
import co.melondev.Snitch.entities.SnitchWorld;
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
 */
@SuppressWarnings("Duplicates")
public enum EnumParam {

    PLAYER(Arrays.asList("player", "players", "p"), "player Turqmelon") {
        @Override
        public void parse(Player player, SnitchQuery query, String[] values) throws Exception {
            for (String playerName : values) {
                SnitchPlayer pl = SnitchPlugin.getInstance().getStorage().getPlayer(playerName);
                Validate.notNull(pl, "Unknown player: " + playerName);
                query.addPlayers(pl);
            }
        }
    },
    ACTION(Arrays.asList("action", "actions", "a"), "action break") {
        @Override
        public void parse(Player player, SnitchQuery query, String[] values) {
            for (String action : values) {
                List<EnumAction> results = EnumAction.getByName(action);
                Validate.notEmpty(results, "Unknown action: " + action);
                for (EnumAction a : results) {
                    query.addActions(a);
                }
            }
        }
    },
    SINCE(Arrays.asList("since", "from", "s"), "since 1d") {
        @Override
        public void parse(Player player, SnitchQuery query, String[] values) {
            Validate.notEmpty(values, "No time specified for " + name() + ".");
            Validate.isTrue(values.length == 1, "Multiple time values provided for " + name() + ".");
            query.setSinceTime(EnumParam.getUnixTime(values[0], false));
        }
    },
    BEFORE(Arrays.asList("before", "prior", "b"), "before 06/12/18") {
        @Override
        public void parse(Player player, SnitchQuery query, String[] values) {
            Validate.notEmpty(values, "No time specified for " + name() + ".");
            Validate.isTrue(values.length == 1, "Multiple time values provided for " + name() + ".");
            query.setBeforeTime(EnumParam.getUnixTime(values[0], false));
        }
    },
    WORLD(Arrays.asList("world", "w"), "world world_nether") {
        @Override
        public void parse(Player player, SnitchQuery query, String[] values) throws Exception {
            Validate.notEmpty(values, "No world specified.");
            Validate.isTrue(values.length == 1, "Multiple world values provided.");
            World world = Bukkit.getWorld(values[0]);
            Validate.notNull(world, "Unknown world: " + values[0]);
            SnitchWorld w = SnitchPlugin.getInstance().getStorage().register(world);
            query.setWorld(w);
        }
    },
    COORDS(Arrays.asList("coords", "relative", "pos", "position"), "relative 100 150 100") {
        @Override
        public void parse(Player player, SnitchQuery query, String[] values) throws Exception {
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
    RADIUS(Arrays.asList("area", "radius", "range"), "area 20") {
        @Override
        public void parse(Player player, SnitchQuery query, String[] values) throws Exception {
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

    public abstract void parse(Player player, SnitchQuery query, String[] values) throws Exception;

}
