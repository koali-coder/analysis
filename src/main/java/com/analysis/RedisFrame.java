package com.analysis;

import java.util.ArrayList;
import java.util.List;

public class RedisFrame {

    private int count = 0;
    List<String> data = null;

    public RedisFrame(int count) {
        this.count = count;
        this.data = new ArrayList<String>(count);
    }

    public void appendArgs(byte[] args) {
        this.data.add(new String(args));
    }

    public int getArgsCount() {
        return data.size();
    }

    public String getFirstArgs() {
        if (data.size() > 0) {
            return data.get(0);
        }
        return null;
    }

    public String getArgs(int index) {
        if (data.size() > index) {
            return data.get(index);
        }
        return null;
    }

}
