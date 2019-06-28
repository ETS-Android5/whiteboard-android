package com.herewhite.sdk.domain;

import com.herewhite.sdk.PptConverter;

public class ConvertedPpt {
    private String taskId;
    private PptConverter.PptType type;
    private Double width;
    private Double height;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public PptConverter.PptType getType() {
        return type;
    }

    public void setType(PptConverter.PptType type) {
        this.type = type;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String[] getSlideURLs() {
        return slideURLs;
    }

    public void setSlideURLs(String[] slideURLs) {
        this.slideURLs = slideURLs;
    }

    public Scene[] getScenes() {
        return scenes;
    }

    public void setScenes(Scene[] scenes) {
        this.scenes = scenes;
    }

    private String[] slideURLs;
    private Scene[] scenes;
}
