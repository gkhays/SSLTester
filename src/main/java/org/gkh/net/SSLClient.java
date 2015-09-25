package org.gkh.net;

import javax.net.ssl.*;

class MyHandshakeListener implements HandshakeCompletedListener {
	public void handshakeCompleted(HandshakeCompletedEvent e) {
		System.out.println("Handshake succesful!");
		System.out.println("Using cipher suite: " + e.getCipherSuite());
	}
}

public class SSLClient extends AbstractSocket {
	public SSLClient(String host, int port, boolean useNss, String path,
			String secret, String keypass) throws Exception {
		initialize(host, port, useNss, path, secret, keypass);
	}

	static void usage() {
		System.out.println(
				"TestClient <host> <port> <path> <secret> [<useNss>]");
	}

	public static void main(String[] args) {
		if (args.length < 4) {
			usage();
			return;
		}

		// TODO - How about an XML or JSON configuration file?
		System.out.println("Starting Java test client...");

		String host = args[0];
		int port = Integer.parseInt(args[1]);

		String path = args[2];
		String secret = args[3];

		boolean useNss = false;
		if (args.length == 5)
			useNss = Boolean.parseBoolean(args[4]);

		String keypass = null;
		if (args.length == 6)
			keypass = args[5];

		try {
			SSLClient client = new SSLClient(host, port, useNss, path, secret,
					keypass);
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
