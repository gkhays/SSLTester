package org.gkh.net.ssl;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.gkh.text.StringUtil;

public class AbstractSocket {
	
	public static final String DEFAULT_ALGORITHM = "TLSv1.2";
	
	protected static Provider pkcs11Provider;
	
    protected SSLContext ctx = null;
	protected KeyStore jks = null;
	protected TrustManagerFactory trustManagerFactory = null;

    //private static String DIR = System.getProperty("DIR");
	protected int port;
	protected String host;
	protected String algorithm = DEFAULT_ALGORITHM;
	protected boolean isServer = false;
	protected SSLSocket socket;
	
	public void enablePkcsProvider(String configFile) {
		pkcs11Provider = new sun.security.pkcs11.SunPKCS11(configFile);
		Security.insertProviderAt(pkcs11Provider, 1);
		new com.sun.net.ssl.internal.ssl.Provider(pkcs11Provider);
		// TODO - These look like candidates for a logger.
		// System.out.println("FIPS Enabled");
		// System.out.println(pkcs11Provider.getName());
		// System.out.println(pkcs11Provider.getInfo());
	}
	
	protected void initialize(String host, int port, boolean useNss,
			String path, String secret, String keypass) throws Exception {
		if (StringUtil.isNullOrEmpty(path))
			throw new Exception(
					"Please specify a valid path for NSS or a Java Keystore");

		this.host = host;
		this.port = port;
		algorithm = DEFAULT_ALGORITHM;
		
		// SSLContext Algorithms {SSL, SSLv2, SSLv3, TLS, TLSv1, TLSv1.1}
		ctx = SSLContext.getInstance(algorithm);

		// TODO - This is extra hacky, but I need to test with cacerts. Also
		// note comments in else-condition below.
		if (path.equals("cacerts")) {
			ctx.init(null, null, null); // Default JRE keystore.
			SSLSocketFactory factory = ctx.getSocketFactory();
			this.socket = (SSLSocket) factory.createSocket(host, port);
			return;
		}

		KeyManagerFactory kmf = null;
		TrustManagerFactory tmf = null;
		kmf = KeyManagerFactory.getInstance("SunX509");
		tmf = TrustManagerFactory.getInstance("SunX509");

		char[] passphrase = secret.toCharArray();

		if (true == useNss) {
			// It is possible to update the provider list in java.security, but
			// we have chosen to do it on-the-fly instead.
			enablePkcsProvider(path);

			// Server gets TrustStore from PKCS#11 token.
			jks = KeyStore.getInstance("PKCS11", "SunPKCS11-NSSFIPS");
			jks.load(null, passphrase);
		} else {
			// To just use the default Java keystore:
			// ctx.init(null, null, null);
			this.jks = KeyStore.getInstance("JKS");
			// InputStream in = new FileInputStream(path);
			InputStream in = AbstractSocket.class.getResourceAsStream("/"
					+ path);
			jks.load(in, passphrase);
		}

		if (StringUtil.isNullOrEmpty(keypass)) {
			kmf.init(jks, passphrase);
		} else {
			kmf.init(jks, keypass.toCharArray());
		}
		tmf.init(jks);

		ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
		// TODO - Log this will you?!
		System.out.println("Configured Protocol: " + ctx.getProtocol());
		System.out.println("Provider:\n  Info: " + ctx.getProvider().getInfo()
				+ "\n  Name: " + ctx.getProvider().getName());

		// SslSocketUtils.printCertificateInformation(alias);
		System.out.println("Locating socket factory for SSL...");
		if (true == isServer) {
			// System.out.println("Starting server socket on port " + port +
			// "...");
			// SSLServerSocketFactory serverFactory =
			// ctx.getServerSocketFactory();
			// SSLServerSocket serverSocket = (SSLServerSocket) serverFactory
			// .createServerSocket(port);
			// serverSocket.setEnabledProtocols(new String[] { "TLSv1" });
		} else {
			if (host == null) {
				throw new Exception("The host must not be null");
			}
			SSLSocketFactory factory = ctx.getSocketFactory();
			System.out
					.println("Creating secure socket to " + host + ":" + port);
			this.socket = (SSLSocket) factory.createSocket(host, port);
			// this.socket.setEnabledProtocols(new String[] { "TLSv1", "SSLv3",
			// "SSLv2Hello" });
		}
	}
    
	/**
	 * Sets the algorithm name for the {@link SSLContext}.
	 * 
	 * <pre>
	 * SSL	Supports some version of SSL; may support other versions
	 * SSLv2	Supports SSL version 2 or later; may support other versions
	 * SSLv3	Supports SSL version 3; may support other versions
	 * TLS	Supports some version of TLS; may support other versions
	 * TLSv1	Supports RFC 2246: TLS version 1.0 ; may support other versions
	 * TLSv1.1	Supports RFC 4346: TLS version 1.1 ; may support other versions
	 * TLSv1.2	Supports RFC 5246: TLS version 1.2 ; may support other versions
	 * </pre>
	 * 
	 * @see <a
	 *      href="https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#SSLContext">Java
	 *      Cryptography Architecture Standard Algorithm Name Documentation</a>
	 * 
	 * @param algorithm
	 *            the algorithm name to specify when generating an instance of
	 *            {@link SSLContext}
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public SSLSocket getSocket() {
		return this.socket;
	}
	
	// TODO - Instead of printing this, let's log it!
	public static void printEnabledArray(String[] enabled, String what) {
		System.out.println("Enabled " + what + ":");
		for (String s : enabled) {
			System.out.println("  " + s);
		}
	}
}
