package com.yzz.bls;

import com.herumi.mcl.Fr;
import com.herumi.mcl.G1;
import com.herumi.mcl.G2;
import com.herumi.mcl.Mcl;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

public class PrivateKey implements java.security.PrivateKey{
    private int curveType;
    private byte[] secKey;

    @Override
    public String getAlgorithm() {
        if(curveType == Bls.BLS12_381) {
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
        Q.setStr(Bls.BaseG1);
        G1 pub = new G1();
        Fr fr = new Fr();
        fr.deserialize(secKey);
        Mcl.mul(pub, Q, fr);

        PublicKey publicKey = new PublicKey(curveType, pub.serialize());
        return publicKey;
    }
    /*
     * Sign a message
     */
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
