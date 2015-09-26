package org.gkh.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;

import javax.net.ssl.*;

class MyHandshakeListener implements HandshakeCompletedListener {
	public void handshakeCompleted(HandshakeCompletedEvent e) {
		System.out.println("Handshake succesful!");
		System.out.println("Using cipher suite: " + e.getCipherSuite());
	}
}

public class SSLClient extends AbstractSocket {

	// TODO - Move this into AbstractSocket or a configuration class.
	public static final String SSL_CONF = "/sslconf.properties";

	public SSLClient(String host, int port, String keyPass) {
		KeyManagerFactory kmf = null;
		TrustManagerFactory tmf = null;
		try {
			kmf = KeyManagerFactory.getInstance("SunX509");
			tmf = TrustManagerFactory.getInstance("SunX509");
			KeyStore jks = KeyStore.getInstance("JKS");
			
			// Per http://stackoverflow.com/questions/22104680/cant-load-a-jks-file-from-classpath
			// We should load the keystore with the following:
			// this.class.getClassLoader().getResourceAsStream("lutum.jks");
			// Except getClassLoader() is not exposed to us. :(
			//InputStream in = SSLClient.class.getResourceAsStream("/keystore.jks");
			InputStream in = this.getClass().getResourceAsStream("/keystore.jks");
			jks.load(in, keyPass.toCharArray());
			kmf.init(jks, keyPass.toCharArray());
			tmf.init(jks);
			//this.ctx = SSLContext.getDefault();
			this.ctx = SSLContext.getInstance("TLSv1");
			ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			SSLSocketFactory factory = ctx.getSocketFactory();
			this.socket = (SSLSocket) factory.createSocket(host, port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public SSLClient(String host, int port, boolean useNss, String path,
			String secret, String keypass) throws Exception {
		initialize(host, port, useNss, path, secret, keypass);
	}

	static void usage() {
		System.out
				.println("SSLClient <host> <port> <path> <secret> [<useNss>]");
	}

	public static void main(String[] args) {
//		if (args.length < 4) {
//			usage();
//			return;
//		}

		// TODO - How about an XML or JSON configuration file?
		System.out.println("Starting Java test client...");		

		String path = null;
		String secret = null;
		if (args.length >= 4) {
			path = args[0];
			secret = args[1];
		}

		boolean useNss = false;
		if (args.length == 3)
			useNss = Boolean.parseBoolean(args[4]);

		String keypass = null;
		if (args.length == 2)
			keypass = args[3];
		
		Properties properties = new Properties();
		InputStream inStream = SSLClient.class.getResourceAsStream(SSL_CONF);
		try {
			properties.load(inStream);
			inStream.close();
		} catch (IOException e) {
			System.err.printf("Error accessing configuration file (%s): %s\n",
					SSL_CONF, e.getMessage());
			System.exit(1);
		}
		String host = properties.getProperty("host");
		String password = properties.getProperty("password");
		int port = Integer.parseInt(properties.getProperty("port"));

		SSLClient client;
		try {
			if (path != null && secret != null) {
				client = new SSLClient(host, port, useNss, path, secret,
						keypass);
			} else {
				client = new SSLClient(host, port, password);
			}
			SSLSocket socket = client.getSocket();

			System.out.println("Supported Cipher Suites: ");
			for (String s : socket.getSupportedCipherSuites()) {
				System.out.println(s);
			}

			System.out.println("Registering a handshake listener...");
			socket.addHandshakeCompletedListener(new MyHandshakeListener());

			System.out.println("Supported protocols:");
			for (String s : socket.getEnabledProtocols())
				System.out.println(s);

			System.out.println("Starting handshaking...");
			socket.setUseClientMode(true);
			socket.startHandshake();
			System.out.println("Just connected to "
					+ socket.getRemoteSocketAddress());

			String[] enabled = null;
			enabled = socket.getEnabledCipherSuites();
			printEnabledArray(enabled, "cipher suites");
			enabled = socket.getEnabledProtocols();
			printEnabledArray(enabled, "protocols");

			new ClientThread(socket).start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
