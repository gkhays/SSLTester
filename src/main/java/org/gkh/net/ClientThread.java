package org.gkh.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocket;

public class ClientThread extends Thread {

	private SSLSocket socket;
	private InputStream in;
	private OutputStream out;
	private Logger logger;

	public ClientThread(SSLSocket socket) throws IOException {
		this.socket = socket;
		this.in = socket.getInputStream();
		this.out = socket.getOutputStream();
		// TODO - Convert to Log4J
		this.logger = Logger.getLogger(ClientThread.class.getName());
	}

	public void run() {
		try {
			String line;
			int available = 0;
			BufferedReader cmdInput = new BufferedReader(
					new InputStreamReader(System.in));
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(this.in));
			PrintWriter writer = new PrintWriter(this.out);
			System.out.println("Type CTRL-Z to exit");

			while ((line = cmdInput.readLine()) != null) {
				logger.info("Received: " + line);
				writer.println(line);
				writer.flush();

				if ((available = in.available()) > 0) {
					logger.info("Available bytes: " + available);
					System.out.println("Server: " + reader.readLine());
				}
			}

			cmdInput.close();
			this.out.close();
			this.in.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
