package me.theredcat.betterespawn;

import java.util.concurrent.TimeUnit;

public class TimeFormatter {

    public static String formatTime(int time) {
        StringBuilder builder = new StringBuilder();

        if(time>=3600){
            long hours = TimeUnit.SECONDS.toHours(time);
            builder.append(hours+"h ");
            time-=hours*3600;
        }

        if(time>=60){
            long minutes = TimeUnit.SECONDS.toMinutes(time);
            builder.append(minutes+"m ");
            time-=minutes*60;
        }

        if (time>=1)
            builder.append(time+"s");

        return builder.toString();
    }
    
}
