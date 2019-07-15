package com.lucky.dangerinfo;

public class CheckDanger {


    static {
        System.loadLibrary("check");
    }

    private static final CheckDanger ourInstance = new CheckDanger();

    public static CheckDanger getInstance() {
        return ourInstance;
    }

    private CheckDanger() {
    }
}
