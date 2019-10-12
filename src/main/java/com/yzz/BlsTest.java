package com.yzz;

import com.herumi.mcl.*;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import java.io.IOException;
import java.lang.reflect.Field;

/*
	BlsTest
*/
public class BlsTest {
	static {
		String lib = "mcljava";
		try {
			addDir("/Users/yuezengzhen/git/mcl/lib");//Add your path of the mcljava lib
		} catch (IOException e) {
			e.printStackTrace();
		}
		String libName = System.mapLibraryName(lib);
		System.out.println("libName : " + libName);
		System.loadLibrary(lib);
	}
	public static int errN = 0;
	public static void assertEquals(String msg, String x, String y) {
		if (x.equals(y)) {
			System.out.println("OK : " + msg);
		} else {
			System.out.println("NG : " + msg + ", x = " + x + ", y = " + y);
			errN++;
		}
	}
	public static void assertBool(String msg, boolean b) {
		if (b) {
			System.out.println("OK : " + msg);
		} else {
			System.out.println("NG : " + msg);
			errN++;
		}
	}
	public static void testCurve(int curveType, String name) {
		try {
			System.out.println("curve=" + name);
			Mcl.SystemInit(curveType);
			Fr x = new Fr(5);
			Fr y = new Fr(-2);
			Fr z = new Fr(5);
			assertBool("x != y", !x.equals(y));
			assertBool("x == z", x.equals(z));
			assertEquals("x == 5", x.toString(), "5");
			Mcl.add(x, x, y);
			assertEquals("x == 3", x.toString(), "3");
			Mcl.mul(x, x, x);
			assertEquals("x == 9", x.toString(), "9");
			assertEquals("x == 12", (new Fr("12")).toString(), "12");
			assertEquals("x == 18", (new Fr("12", 16)).toString(), "18");
			assertEquals("x == ff", (new Fr("255")).toString(16), "ff");

			{
				byte[] b = x.serialize();
				Fr t = new Fr();
				t.deserialize(b);
				assertBool("serialize", x.equals(t));
			}
			G1 P = new G1();
			System.out.println("P=" + P);
			Mcl.hashAndMapToG1(P, "test".getBytes());
			System.out.println("P=" + P);
			byte[] buf = { 1, 2, 3, 4 };
			Mcl.hashAndMapToG1(P, buf);
			System.out.println("P=" + P);
			Mcl.neg(P, P);
			System.out.println("P=" + P);
			{
				byte[] b = P.serialize();
				G1 t = new G1();
				t.deserialize(b);
				assertBool("serialize", P.equals(t));
			}

			G2 Q = new G2();
			Mcl.hashAndMapToG2(Q, "abc".getBytes());
			System.out.println("Q=" + Q);

			Mcl.hashAndMapToG1(P, "This is a pen".getBytes());
			{
				String s = P.toString();
				G1 P1 = new G1();
				P1.setStr(s);
				assertBool("P == P1", P1.equals(P));
			}
			{
				byte[] b = Q.serialize();
				G2 t = new G2();
				t.deserialize(b);
				assertBool("serialize", Q.equals(t));
			}

			GT e = new GT();
			Mcl.pairing(e, P, Q);
			GT e1 = new GT();
			GT e2 = new GT();
			Fr c = new Fr("1234567890123234928348230428394234");
			System.out.println("c=" + c);
			G2 cQ = new G2(Q);
			Mcl.mul(cQ, Q, c); // cQ = Q * c
			Mcl.pairing(e1, P, cQ);
			Mcl.pow(e2, e, c); // e2 = e^c
			assertBool("e1 == e2", e1.equals(e2));
			{
				byte[] b = e1.serialize();
				GT t = new GT();
				t.deserialize(b);
				assertBool("serialize", e1.equals(t));
			}
			G1 cP = new G1(P);
			Mcl.mul(cP, P, c); // cP = P * c
			Mcl.pairing(e1, cP, Q);
			assertBool("e1 == e2", e1.equals(e2));

			BLSsignature(Q);
			if (errN == 0) {
				System.out.println("all test passed");
			} else {
				System.out.println("ERR=" + errN);
			}
		} catch (RuntimeException e) {
			System.out.println("unknown exception :" + e);
		}
	}
	public static void BLSsignature(G2 Q)
	{
		Fr s = new Fr();
		s.setByCSPRNG(); // secret key
		System.out.println("secret key " + s.serialize().length);
		G2 pub = new G2();
		Mcl.mul(pub, Q, s); // public key = sQ
		byte[] t = pub.serialize();

		System.out.println("the length of public key : " + HexBin.encode(t));

		byte[] m = "signature test".getBytes();
		G1 H = new G1();
		Mcl.hashAndMapToG1(H, m); // H = Hash(m)
		G1 sign = new G1();
		Mcl.mul(sign, H, s); // signature of m = s H
        byte[] g = sign.serialize();
		System.out.println("the length of signature : " + HexBin.encode(g));
		GT e1 = new GT();
		GT e2 = new GT();
		Mcl.pairing(e1, H, pub); // e1 = e(H, s Q)
		Mcl.pairing(e2, sign, Q); // e2 = e(s H, Q);
		System.out.println("my " + sign.toString());
		assertBool("verify signature", e1.equals(e2));
	}

	public static void testBLSsignature(int curveType, String name) {
		try {
			System.out.println("curve=" + name);
			Mcl.SystemInit(curveType);
			Fr[] ss = new Fr[10];
			for (int i = 0; i < 10; i++) {
				ss[i] = new Fr();
				ss[i].setByCSPRNG();
				//System.out.println(HexBin.encode(ss[i].serialize()));
			}
			long start = System.currentTimeMillis();
			G1[] pubs = new G1[10];
			G1 Q = new G1();
			Q.deserialize(HexBin.decode("282899BF4430ADD41BDCE37577237ED1CCF1D1DD8F035ABEFC5CB4B2F5C8845F45257BACD6C4019535B0DC651084FF02"));
			for (int i = 0; i < 10; i++) {
				pubs[i] = new G1();
				Mcl.mul(pubs[i], Q, ss[i]); // public key = sQ
				//System.out.println(HexBin.encode(pubs[i].serialize()));
			}
			System.out.printf("提取公钥耗时 %d \n", System.currentTimeMillis() - start );
			byte[] m = "signature test".getBytes();
			G2 H = new G2();
			Mcl.hashAndMapToG2(H, m); // H = Hash(m)
			G2[] signs = new G2[10];
			start = System.currentTimeMillis();
			for (int i = 0; i < 10; i++) {
				signs[i] = new G2();
				Mcl.mul(signs[i], H, ss[i]); // signature of m = s H
				//System.out.println(HexBin.encode(signs[i].serialize()));
			}
			System.out.printf("签名耗时 %d \n", System.currentTimeMillis() - start );
			start = System.currentTimeMillis();
			for (int i = 0; i < 10; i++) {
				GT e1 = new GT();
				GT e2 = new GT();
				Mcl.pairing(e1, pubs[i], H); // e1 = e(H, s Q)
				Mcl.pairing(e2, Q, signs[i]); // e2 = e(s H, Q);
				assertBool("verify signature", e1.equals(e2));
			}
			System.out.printf("单个验证签名耗时 %d \n", System.currentTimeMillis() - start );
			start = System.currentTimeMillis();
			for (int i = 1; i < 10; i++) {
				Mcl.add(signs[0], signs[0], signs[i]);
			}

			for (int i = 1; i < 10; i++) {
				Mcl.add(pubs[0], pubs[0], pubs[i]);
			}
			//System.out.println(HexBin.encode(signs[0].serialize()));
			//System.out.println(HexBin.encode(pubs[0].serialize()));
			GT e1 = new GT();
			GT e2 = new GT();
			Mcl.pairing(e1, pubs[0], H); // e1 = e(H, s Q)
			Mcl.pairing(e2, Q, signs[0]); // e2 = e(s H, Q);
			assertBool("verify signature", e1.equals(e2));
			System.out.printf("聚合验证签名耗时 %d \n", System.currentTimeMillis() - start );
		} catch (RuntimeException e) {
			System.out.println("unknown exception :" + e);
		}
	}

	public static void addDir(String s) throws IOException {
		try {
			Field field = ClassLoader.class.getDeclaredField("usr_paths");
			field.setAccessible(true);
			String[] paths = (String[])field.get(null);
			for (int i = 0; i < paths.length; i++) {
				if (s.equals(paths[i])) {
					return;
				}
			}
			String[] tmp = new String[paths.length+1];
			System.arraycopy(paths,0,tmp,0,paths.length);
			tmp[paths.length] = s;
			field.set(null,tmp);
		} catch (IllegalAccessException e) {
			throw new IOException("Failed to get permissions to set library path");
		} catch (NoSuchFieldException e) {
			throw new IOException("Failed to get field handle to set library path");
		}
	}
	public static void main(String argv[]) {
		//testCurve(Mcl.BN254, "BN254");
		testBLSsignature(Mcl.BLS12_381, "BLS12_381");
	}
}
