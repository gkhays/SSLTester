package org.gkh.net.ssl;

import javax.net.*;
import javax.net.ssl.*;

public class SSLServer extends AbstractSocket {
	volatile int serverPort = 0;

	// List of FIPS cipher suites.
	// http://www.mozilla.org/projects/security/pki/nss/ssl/fips-ssl-ciphersuites.html

	SSLServer() {
		this.isServer = true;
	}
    
	void doServerSide(int port, boolean useNss, String store, String secret,
			String keypass) throws Exception {
		serverPort = port;
		initialize(null, port, useNss, store, secret, keypass);

		ServerSocketFactory ssf = ctx.getServerSocketFactory();
		SSLServerSocket sslServerSocket = (SSLServerSocket) ssf
				.createServerSocket(serverPort);
		sslServerSocket.setEnabledProtocols(new String[] { "TLSv1" });

		String[] ciphers = sslServerSocket.getSupportedCipherSuites();
		System.out.println("Supported Cipher Suites");
		for (int i = 0; i < ciphers.length; i++) {
			System.out.println(ciphers[i]);
		}

		/*
		 * Supported Protocols: SSLv2Hello, SSLv3, TLSv1
		 */
		String[] protocols = sslServerSocket.getSupportedProtocols();
		System.out.println("Supported Protocols");
		for (int i = 0; i < protocols.length; i++) {
			System.out.println(protocols[i]);
		}

		String[] enabled = sslServerSocket.getEnabledCipherSuites();
		System.out.println("Enabled (Server) Cipher Suites");
		for (int i = 0; i < enabled.length; i++) {
			System.out.println(enabled[i]);
		}
		sslServerSocket.setEnabledCipherSuites(ciphers);

		/*
		 * Configured Protocol: TLSv1 Provider: Info: Sun JSSE provider(PKCS12,
		 * SunX509 key/trust factories, SSLv3, TLSv1) Name: SunJSSE
		 */
		System.out.println("Configured Protocol: " + ctx.getProtocol());
		System.out.println("Provider: (info) " + ctx.getProvider().getInfo()
				+ " (name) " + ctx.getProvider().getName());

		sslServerSocket.setNeedClientAuth(true);
		serverPort = sslServerSocket.getLocalPort();
		System.out.println("serverPort = " + serverPort);

		while (true) {
			SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
			new ServerThread(sslSocket).start();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		int port = Integer.parseInt(args[0]);
		String store = "";
		String secret = "";

		if (args.length >= 3) {
			store = args[1];
			secret = args[2];
		}

		boolean useNss = false;
		if (args.length == 4)
			useNss = Boolean.parseBoolean(args[3]);

		String keypass = null;
		if (args.length == 5)
			keypass = args[4];

		System.out.println("Starting Java test server...");
		SSLServer testServer = new SSLServer();

		testServer.doServerSide(port, useNss, store, secret, keypass);
	}

}
