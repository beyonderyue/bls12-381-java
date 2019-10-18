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

}
