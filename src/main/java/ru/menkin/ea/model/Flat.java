package ru.menkin.ea.model;

import java.lang.reflect.*;
import java.util.*;

public class Flat {
    public int rooms;

    public Flat(int rooms) {
        this.rooms = rooms;
    }

    public Flat(Map<String, Object> data) throws Exception {
        for (Field field : Flat.class.getDeclaredFields()) {
            field.set(this, data.get(field.getName()));
        }
    }

    public int getRooms() {
        return rooms;
    }
    public void setRooms(int rooms) {
        this.rooms = rooms;
    }
}
