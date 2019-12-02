package me.android.library.common.enums;


import me.java.library.common.model.po.BaseEnum;

public enum ConnectivityMode implements BaseEnum {

    //@formatter:off
    Unknown     (-1, "unknown"),
    Broken      (0, "broken"),
    Wifi        (1, "wifi"),
    GSM         (2, "2G/3g/4G/5G"),
    //@formatter:on
    ;

    private int value;
    private String name;

    ConnectivityMode(int value, String name) {
        this.value = value;
        this.name = name;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getValue() {
        return value;
    }
}
