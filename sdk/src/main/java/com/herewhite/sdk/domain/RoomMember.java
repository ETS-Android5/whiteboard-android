package com.herewhite.sdk.domain;

/**
 * Created by buhe on 2018/8/13.
 */

public class RoomMember {
    private Long memberId;
    private Boolean isRtcConnected;
    private String currentApplianceName;
    private MemberInformation information;

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public boolean isRtcConnected() {
        return isRtcConnected;
    }

    public void setRtcConnected(boolean rtcConnected) {
        isRtcConnected = rtcConnected;
    }

    public String getCurrentApplianceName() {
        return currentApplianceName;
    }

    public void setCurrentApplianceName(String currentApplianceName) {
        this.currentApplianceName = currentApplianceName;
    }

    public MemberInformation getInformation() {
        return information;
    }

    public void setInformation(MemberInformation information) {
        this.information = information;
    }
}
