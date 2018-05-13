package com.frog.attentionattacher.bean;

import java.util.List;

public class ToDoListBean {
    private String name;
    private int iconName;
    private List<ToDoList> collocationToDoList;

    public static class ToDoList {
        private String content;

        public ToDoList(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconName() {
        return iconName;
    }

    public void setIconName(int iconName) {
        this.iconName = iconName;
    }

    public List<ToDoList> getCollocationToDoList() {
        return collocationToDoList;
    }

    public void setCollocationToDoList(List<ToDoList> collocationToDoList) {
        this.collocationToDoList = collocationToDoList;
    }
}
