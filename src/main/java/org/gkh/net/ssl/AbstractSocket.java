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
	
	protected static Provider pkcs11Provider;
	
    protected SSLContext ctx = null;
	protected KeyStore jks = null;
	protected TrustManagerFactory trustManagerFactory = null;

    //private static String DIR = System.getProperty("DIR");
	protected int port;
	protected String host;
	protected boolean isServer = false;
	protected SSLSocket socket;
	
	public void enablePkcsProvider(String configFile) 
	{		
        pkcs11Provider = new sun.security.pkcs11.SunPKCS11(configFile);
        Security.insertProviderAt(pkcs11Provider, 1);
        new com.sun.net.ssl.internal.ssl.Provider(pkcs11Provider);
//        System.out.println("FIPS Enabled");
//        System.out.println(pkcs11Provider.getName());
//        System.out.println(pkcs11Provider.getInfo());
    }
	
	protected void initialize(String host, int port, boolean useNss,
			String path, String secret, String keypass) throws Exception {
		if (StringUtil.isNullOrEmpty(path))
			throw new Exception(
					"Please specify a valid path for NSS or a Java Keystore");
		
		this.host = host;
		this.port = port;

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
//			InputStream in = new FileInputStream(path);
			InputStream in = AbstractSocket.class.getResourceAsStream("/" + path);
			jks.load(in, passphrase);
		}

		if (StringUtil.isNullOrEmpty(keypass))
			kmf.init(jks, passphrase);
		else
			kmf.init(jks, keypass.toCharArray());
		tmf.init(jks);

		// SSLContext Algorithms {SSL, SSLv2, SSLv3, TLS, TLSv1, TLSv1.1}
		// http://download.oracle.com/javase/6/docs/technotes/guides//security/StandardNames.html#SSLContext
		ctx = SSLContext.getInstance("TLSv1");
		ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

		System.out.println("Configured Protocol: " + ctx.getProtocol());
		System.out.println("Provider:\n  Info: " + ctx.getProvider().getInfo()
				+ "\n  Name: " + ctx.getProvider().getName());

		// SslSocketUtils.printCertificateInformation(alias);
		System.out.println("Locating socket factory for SSL...");
		if (true == isServer) {
//			System.out.println("Starting server socket on port " + port + "...");
//			SSLServerSocketFactory serverFactory = ctx.getServerSocketFactory();
//			SSLServerSocket serverSocket = (SSLServerSocket) serverFactory
//					.createServerSocket(port);
//			serverSocket.setEnabledProtocols(new String[] { "TLSv1" });
		} else {
			if (host == null) {
				throw new Exception("The host must not be null");
			}
			SSLSocketFactory factory = ctx.getSocketFactory();
			System.out
					.println("Creating secure socket to " + host + ":" + port);
			this.socket = (SSLSocket) factory.createSocket(host, port);
//			this.socket.setEnabledProtocols(new String[] { "TLSv1", "SSLv3",
//					"SSLv2Hello" });
		}
	}
    
	public void setHost(String host)
	{
		this.host = host;
	}
	public void setPort(int port)
	{
		this.port = port;
	}
	public SSLSocket getSocket()
	{
		return this.socket;
	}
	
	public static void printEnabledArray(String[] enabled, String what)
	{
		System.out.println("Enabled " + what + ":");
		for (String s : enabled)
		{
			System.out.println("  " + s);
		}
	}
}
