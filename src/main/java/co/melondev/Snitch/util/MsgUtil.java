package co.melondev.Snitch.util;

import co.melondev.Snitch.entities.SnitchEntry;
import co.melondev.Snitch.entities.SnitchQuery;
import org.bukkit.command.CommandSender;

import java.util.List;

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
            for (int i = start; i < end; i++) {
                if (i < entries.size()) {
                    SnitchEntry entry = entries.get(i);
                    String d = entry.getDescriptor();
                    sender.sendMessage(record(d + "§8 (" + i + ")"));
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
