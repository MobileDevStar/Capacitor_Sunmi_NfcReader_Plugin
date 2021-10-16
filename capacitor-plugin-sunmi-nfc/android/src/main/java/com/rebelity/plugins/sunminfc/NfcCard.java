package com.rebelity.plugins.sunminfc;

public class NfcCard {
    private String      decID;
    private String      reversedDecId;
    private String      hexID;
    private String      reversedHexID;

    public NfcCard(String decID, String reversedDecId, String hexID, String reversedHexID) {
        this.decID = decID;
        this.reversedDecId = reversedDecId;
        this.hexID = reversedHexID;
    }

    public String getDecID() {
        return decID;
    }

    public String getReversedDecId() {
        return reversedDecId;
    }

    public String getHexID() {
        return hexID;
    }

    public String getReversedHexID() {
        return reversedHexID;
    }
}
