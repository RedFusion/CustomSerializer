package ru.menkin.ea.serializer;

import java.util.*;

public class Result {
    private Map<String, Object> map = new HashMap<>();
    private int byteIterator;

    public Result(Map<String, Object> map, int byteIterator) {
        this.map = map;
        this.byteIterator = byteIterator;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
    public int getByteIterator() {
        return byteIterator;
    }
    public void setByteIterator(int byteIterator) {
        this.byteIterator = byteIterator;
    }
}
