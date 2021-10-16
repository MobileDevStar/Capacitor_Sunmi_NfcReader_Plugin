package com.rebelity.plugins.sunminfc;

public class NfcCard {
    private String      decId;
    private String      reversedDecId;
    private String      hexId;
    private String      reversedHexId;

    public NfcCard(String decId, String reversedDecId, String hexId, String reversedHexId) {
        this.decId = decId;
        this.reversedDecId = reversedDecId;
        this.hexId = hexId;
        this.reversedHexId = reversedHexId;
    }

    public String getDecId() {
        return decId;
    }

    public String getReversedDecId() {
        return reversedDecId;
    }

    public String getHexId() {
        return hexId;
    }

    public String reversedHexId() {
        return reversedHexId;
    }
}
