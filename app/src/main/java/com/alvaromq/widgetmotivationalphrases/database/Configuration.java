package com.alvaromq.widgetmotivationalphrases.database;

public class Configuration {
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    private String language;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

    public Configuration() {
    }
}
