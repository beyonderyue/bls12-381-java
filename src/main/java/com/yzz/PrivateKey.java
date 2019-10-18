package com.yzz;

import com.herumi.mcl.Fr;
import com.herumi.mcl.G1;
import com.herumi.mcl.G2;
import com.herumi.mcl.Mcl;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

public class PrivateKey implements java.security.PrivateKey{
    private int curveType;
    private byte[] secKey;
    private String QF = "282899BF4430ADD41BDCE37577237ED1CCF1D1DD8F035ABEFC5CB4B2F5C8845F45257BACD6C4019535B0DC651084FF02";

    @Override
    public String getAlgorithm() {
        if(curveType == Mcl.BLS12_381) {
            return "BLS12-381";
        } else {
            return "NOT SUPPORTED";
        }
    }

    @Override
    public String getFormat() {
        return null;
    }

    @Override
    public byte[] getEncoded() {
        return secKey;
    }

    public PrivateKey(int curveType) {
        this.curveType = curveType;
        Fr fr = new Fr();
        fr.setByCSPRNG();
        secKey = fr.serialize();
    }
    public PublicKey getPublicKey() {
        G1 Q = new G1();
        Q.deserialize(HexBin.decode(QF));
        G1 pub = new G1();
        Fr fr = new Fr();
        fr.deserialize(secKey);
        Mcl.mul(pub, Q, fr);

        PublicKey publicKey = new PublicKey(curveType, pub.serialize());
        return publicKey;
    }

    public byte[] sign(byte[] msg) {
        G2 H = new G2();
        Mcl.hashAndMapToG2(H, msg); // H = Hash(m)
        G2 sig = new G2();
        Fr fr = new Fr();
        fr.deserialize(secKey);
        Mcl.mul(sig, H, fr); // signature of m = s H
        return sig.serialize();
    }
}
