package com.pozornik.mypetmon;

import java.util.UUID;

public class Pet {
    private String id;
    private String name;
    private String avatar; // emoji or url
    private String statusText; // e.g., "Последний вес: 12.5 кг"
    private String nextProcedure; // e.g., "Прививка через 12 дней"

    public Pet() {
        this.id = UUID.randomUUID().toString();
    }

    public Pet(String name, String avatar, String statusText, String nextProcedure) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.avatar = avatar;
        this.statusText = statusText;
        this.nextProcedure = nextProcedure;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getStatusText() { return statusText; }
    public void setStatusText(String statusText) { this.statusText = statusText; }

    public String getNextProcedure() { return nextProcedure; }
    public void setNextProcedure(String nextProcedure) { this.nextProcedure = nextProcedure; }
}
