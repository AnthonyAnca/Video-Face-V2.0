package com.example.anthonyanca.videoface;

import java.io.Serializable;

public class Eventos implements Serializable {

    private String eventId;
    private String name;
    private String description;

    public Eventos(){
        //this constructor is required
    }

    public Eventos(String eventId, String name, String description) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
    }

    public String getEventId(){
        return eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescripcion(String description) {
        this.description = description;
    }
}
