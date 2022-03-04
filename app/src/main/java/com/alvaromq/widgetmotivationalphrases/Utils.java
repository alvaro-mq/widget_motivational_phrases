package com.alvaromq.widgetmotivationalphrases;

public class Utils {
    public static String generateNick(String name) {
        return "@" + name.toLowerCase().replaceAll(" ", "_");
    }

    public static String generateNickAvatar(String name) {
        return name.toUpperCase().substring(0, 2);
    }
}
