package co.melondev.Snitch.util;

import co.melondev.Snitch.entities.SnitchEntry;
import co.melondev.Snitch.entities.SnitchPosition;
import co.melondev.Snitch.entities.SnitchQuery;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Devon on 7/13/18.
 */
public class MsgUtil {

    public static void sendRecords(CommandSender sender, SnitchQuery query, List<SnitchEntry> entries, int page, int perPage) {

        sender.sendMessage(info(query.getSearchSummary()));
        if (entries.isEmpty()) {
            sender.sendMessage(error("No records match your search criteria. Maybe try being more broad?"));
        } else {
            int start = perPage * (page - 1);
            int end = start + perPage;
            boolean nextPage = true;
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy hh:mm a");
            for (int i = start; i < end; i++) {
                if (i < entries.size()) {
                    SnitchEntry entry = entries.get(i);
                    String d = entry.getDescriptor();

                    String timestamp;
                    if (System.currentTimeMillis() - entry.getTimestamp() > TimeUnit.DAYS.toMillis(1)) {
                        timestamp = df.format(entry.getTimestamp()) + ":";
                    } else {
                        timestamp = TimeUtil.formatDateDiff(entry.getTimestamp(), true) + " ago:";
                    }

                    String rawMsg = record("§e" + timestamp + " §7" + d + "§8 (" + i + ")");
                    if ((sender instanceof Player)) {
                        SnitchPosition p = entry.getSnitchPosition();
                        ComponentBuilder builder = new ComponentBuilder("§7Action: §6" + entry.getAction().getName() + "§e (" + entry.getAction().name() + ")\n" +
                                "§7Actor: §6" + entry.getSnitchPlayer().getPlayerName() + "\n" +
                                "§7World: §6" + entry.getSnitchWorld().getWorldName() + "\n" +
                                "§7Location: §6" + p.getX() + "x, " + p.getY() + "y, " + p.getZ() + "z\n" +
                                "§7Time: §6" + df.format(entry.getTimestamp()) + "§e (" + TimeUtil.formatDateDiff(entry.getTimestamp(), false) + " ago)\n" +
                                "§f\n§fLeft-Click§7 to teleport to this event.");
                        BaseComponent[] c = new ComponentBuilder(rawMsg)
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, builder.create()))
                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/snitch tp " + i)).create();
                        ((Player) sender).spigot().sendMessage(c);
                    } else {
                        sender.sendMessage(rawMsg);
                    }

                } else {
                    nextPage = false;
                    break;
                }
            }
            if (nextPage) {
                sender.sendMessage(info("For more: §6/snitch next§e or §6/snitch prev"));
            }
        }

    }

    public static void staff(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("snitch.notify")) {
                player.sendMessage("§f§l[Snitch] §b" + message);
            }
        }
    }

    public static String record(String message) {
        return "§f> §7" + message;
    }

    public static String info(String message) {
        return "§f§l[Snitch] §e" + message;
    }

    public static String success(String message) {
        return "§f§l[Snitch] §a" + message;
    }

    public static String error(String message) {
        return "§f§l[Snitch] §c" + message;
    }

}
