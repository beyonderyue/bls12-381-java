package com.yzz;

import com.herumi.mcl.Fr;
import com.herumi.mcl.Mcl;

public class Bls {
    static {
        String lib = "mcljava";
        JNIDevelopment deve = new JNIDevelopment();
        deve.doDefaultDevelopment();
        String libName = System.mapLibraryName(lib);
        System.loadLibrary(lib);
    }
    private int curveType;
    public PrivateKey generateSecKey() {
        PrivateKey privateKey = new PrivateKey(curveType);
        return privateKey;
    }
    public Bls(int curveType) {
        this.curveType = curveType;
        Mcl.SystemInit(curveType);
    }
}
