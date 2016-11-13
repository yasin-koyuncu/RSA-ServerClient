import java.io.*;
import java.net.*;
import java.util.*;
import java.math.*;
import java.util.regex.*;

public class P2PTCPClient {

	private static String E, N;
	private static BigInteger d, Nmake;
	private static int secretNumber;

	public static void main(String[] args) {
		Scanner scan;
		Thread st = null;
		Socket peerConnectionSocket = null;
		if (args[0].equals("server")) {
			try {
				ServerSocket ss = new ServerSocket(Integer.parseInt(args[1]));
				System.out.println("Waiting for connection...");
				peerConnectionSocket = ss.accept();

				st = new Thread(new StringSender(new PrintWriter(
						peerConnectionSocket.getOutputStream())));
				st.start();
				scan = new Scanner(peerConnectionSocket.getInputStream());
				String fromSocket;
				while ((fromSocket = scan.nextLine()) != null)
					System.out.println(fromSocket);
			} catch (IOException e) {
				System.err.println("Server crash");
			} finally {
				st.stop();
			}
		} else if (args[0].equals("client")) {



			BigInteger one = new BigInteger("1");
			BigInteger two = new BigInteger("2");
			BigInteger E = new BigInteger("3");

			boolean foundE = false;
			boolean foundD = false;

			BigInteger size = new BigInteger(args[3]);
			size = size.divide(two);

			BigInteger b = new BigInteger("" + size);

			System.out.println("size: " + size);
			System.out.println("size: " + size.bitCount());

			int bitLength = b.bitLength();
			System.out.println("BL : " + bitLength);

			BigInteger p1 = generatePrimes(size);
			BigInteger p2 = generatePrimes(size);

			System.out.println("P1: " + p1 + " P2: " + p2);

			Nmake = p1.multiply(p2);

			System.out.println("N " + Nmake);

			BigInteger phi = (p1.subtract(one)).multiply(p2.subtract(one));
			System.out.println("phi:  " + phi);

			while (!foundE) {

				if (phi.gcd(E).equals(one)) {
					foundE = true;
				} else {
					E = E.add(one).add(one);
				}
			}
			System.out.println("e: " + E);

			BigInteger k = new BigInteger("1");
			BigInteger tmp = new BigInteger("0");

			while (!foundD) {

				tmp = k.multiply(phi);
				tmp = tmp.add(one);

				if (!tmp.gcd(E).equals(one))
					foundD = true;
				else
					k = k.add(one);

			}

			System.out.println("k: " + k);

			tmp = tmp.divide(E);

			d = new BigInteger(tmp.toString());

			System.out.println("d: " + d);

			try {
				peerConnectionSocket = new Socket(args[1],
						Integer.parseInt(args[2]));

				// st = new Thread(new StringSender(new
				// PrintWriter(peerConnectionSocket.getOutputStream())),publicKey);
				// st.start();
				PrintWriter out = new PrintWriter(
						peerConnectionSocket.getOutputStream());
				scan = new Scanner(peerConnectionSocket.getInputStream());
				String publicKey, tmp2;

				if ((publicKey = scan.nextLine()) != null)
					System.out.println(publicKey);

				sscanf(publicKey);

				secretNumber = 45;
				out.println(encrypt("" + secretNumber));
				out.flush();

				if ((tmp2 = scan.nextLine()) != null)
					System.out.println(tmp2);

				if (secretNumber == Integer.parseInt(tmp2)) {
					System.out.println("Same number");
				}

					// System.out.println("Waiting for connection....");
					// peerConnectionSocket = ss.accept();

					// st = new Thread(new StringSender(new
					// PrintWriter(peerConnectionSocket.getOutputStream())));
					// st.start();
					
					out.println("#" + E + "#" + Nmake + "#");
					out.flush();
					scan = new Scanner(peerConnectionSocket.getInputStream());
					String fromSocket;
					/*
					 * while((fromSocket = scan.nextLine())!=null){
					 * System.out.println(fromSocket);
					 * System.out.println(decrypt(fromSocket)); }
					 */
					if((fromSocket=scan.nextLine())!=null){
					    System.out.println(decrypt(fromSocket));
					    out.println(decrypt(fromSocket));
					    out.flush();
					}


				

			} catch (Exception e) {
				System.err.println("Client crash");
			} finally {
				st.stop();
			}
		}
	}

	public static void sscanf(String publicKey) {

		Pattern p = Pattern.compile("#(\\d+)#(\\d+)#");
		Matcher m = p.matcher(publicKey);

		while (m.find()) {

			if (m.group().length() != 0) {
				E = m.group(1);
				N = m.group(2);
			}

		}
		System.out.println("E " + E);
		System.out.println("N " + N);

	}

	public static String encrypt(String str) {

		BigInteger C = new BigInteger(str);
		BigInteger publicE = new BigInteger(E);
		BigInteger publicN = new BigInteger(N);

		C = C.modPow(publicE, publicN);

		return C.toString();

	}

	public static String decrypt(String ciphertext) {

		BigInteger msg = new BigInteger(ciphertext);

		msg = msg.modPow(d, Nmake);

		return msg.toString();

	}

	public static BigInteger generatePrimes(BigInteger size) {

		BigInteger prime = new BigInteger("0");

		while (true) {
			prime = BigInteger.probablePrime(size.bitLength(), new Random());
			if (prime.compareTo(size) == 1 && prime.isProbablePrime(15)) {
				System.out.println("Found a suitable prime");
				return prime;
			}
		}
	}
}
