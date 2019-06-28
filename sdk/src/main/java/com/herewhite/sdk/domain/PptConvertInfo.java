package com.herewhite.sdk.domain;

public class PptConvertInfo extends WhiteObject {

    public enum ServerConversionStatus {
        Waiting,
        Converting,
        NotFound,
        Finished,
        Fail
    }

    public PptPage[] getStaticConversionFileList() {
        return staticConversionFileList;
    }

    public String getReason() {
        return reason;
    }

    public ServerConversionStatus getConvertStatus() {
        return convertStatus;
    }

    public String getPrefix() {
        return prefix;
    }

    public Integer getTotalPageSize() {
        return totalPageSize;
    }

    public Integer getConvertedPageSize() {
        return convertedPageSize;
    }

    public Double getConvertedPercentage() {
        return convertedPercentage;
    }

    private PptPage[] staticConversionFileList;
    private String reason;
    private ServerConversionStatus convertStatus;
    private String prefix;
    private Integer totalPageSize;
    private Integer convertedPageSize;
    private Double convertedPercentage;
}
