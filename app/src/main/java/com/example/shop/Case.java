package com.example.shop;

import java.lang.reflect.Array;
import java.util.List;

public class Case {
    private String id;
    private String name;
    private String info;
    private List<String> date;

    public Case(String name, String info, List<String> date) {
        this.name = name;
        this.info = info;
        this.date = date;
    }

    public Case() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<String> getDate() {
        return date;
    }

    public void setDate(List<String> date) {
        this.date = date;
    }

    public String _getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
