package com.yzz;

import com.herumi.mcl.Mcl;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

public class BlsTest extends TestCase {


    @Test
    public void testBls() {
        Bls bls = new Bls(Mcl.BLS12_381);
        PrivateKey privateKey = bls.generateSecKey();
        System.out.println(HexBin.encode(privateKey.getEncoded()));

        byte[] sig = privateKey.sign("Hello 1world".getBytes());
        System.out.println(HexBin.encode(sig));
        assertTrue(privateKey.getPublicKey().verify("Hello 1world".getBytes(), sig));

    }

    @Test
    public void testAggregateMsg() {
        byte[][] msg = {"hello".getBytes(),"world".getBytes(),"!".getBytes()};
        Bls bls = new Bls(Mcl.BLS12_381);
        PrivateKey privateKey = bls.generateSecKey();
        byte[][] sigs = new byte[msg.length][];
        for (int i=0;i<msg.length;i++) {
            sigs[i] = privateKey.sign(msg[i]);
        }

        byte[] asig = bls.aggregateSignature(sigs);
        byte[] amsg = bls.aggregateMsg(msg);

        PublicKey publicKey = privateKey.getPublicKey();
        assertTrue(publicKey.verifyAggregate(amsg, asig));
    }

    @Test
    public void testAggregateSignature() {
        byte[] msg = "Hello world !".getBytes();
        Bls bls = new Bls(Mcl.BLS12_381);
        PrivateKey[] privateKeys = new PrivateKey[10];
        byte[][] sigs = new byte[privateKeys.length][];
        byte[][] pubs = new byte[privateKeys.length][];
        for(int i=0;i<privateKeys.length;i++) {
            privateKeys[i] = bls.generateSecKey();
            sigs[i] = privateKeys[i].sign(msg);
            pubs[i] = privateKeys[i].getPublicKey().getEncoded();
        }
        byte[] sig = bls.aggregateSignature(sigs);
        byte[] pub = bls.aggregatePublicKey(pubs);
        PublicKey newPub = new PublicKey(Mcl.BLS12_381, pub);
        assertTrue(newPub.verify(msg, sig));
    }

}
