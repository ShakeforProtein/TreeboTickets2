package me.shakeforprotein.treebotickets;

import org.bukkit.ChatColor;

public class BasicHelperMethods {

    public boolean isNumeric (String str) {return str.matches("\\d+");}

    public String formatTime(long millis) {
        if (millis == 0) {
            return "0S";
        } else {
            long seconds = Math.round((double) millis / 1000);
            long days = seconds / 86400;
            seconds = seconds - (days * 86400);
            long hours = seconds / 3600;
            seconds = seconds - (hours * 3600);
            long minutes = seconds / 60;
            seconds = seconds - (minutes * 60);
            String output = "";
            if (days > 0) {
                output += days + "D:" + hours + "H:" + minutes + "M:" + seconds + "S";
            } else if (hours > 0) {
                output += hours + "H:" + minutes + "M:" + seconds + "S";
            } else if (minutes > 0) {
                output += minutes + "M:" + seconds + "S";
            } else if (seconds > 0) {
                output += seconds + "S";
            } else {
                output += "ERROR ERROR ERROR ERROR ERROR";
            }

            return output;
        }
    }

    public String getSeverityColour(long lastOff) {
        int oneDay = 1000 * 60 * 60 * 24;
        long currentTime = System.currentTimeMillis();
        String severityColour;
        if (currentTime - lastOff > (oneDay * 5)) {
            severityColour = ChatColor.RED + "";
        } else if (currentTime - lastOff > (oneDay * 3)) {
            severityColour = ChatColor.YELLOW + "";
        } else {
            severityColour = ChatColor.GREEN + "";
        }
        return severityColour;
    }
}
