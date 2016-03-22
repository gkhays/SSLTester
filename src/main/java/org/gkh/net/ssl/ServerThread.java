package org.gkh.net.ssl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocket;

public class ServerThread extends Thread {
  
	private SSLSocket socket;
	private InputStream in;
	private OutputStream out;
	private Logger logger;
	private boolean isRunning;

	public ServerThread(SSLSocket socket) throws IOException {
		this.socket = socket;
		this.in = socket.getInputStream();
		this.out = socket.getOutputStream();
		this.isRunning = true;
		this.logger = Logger.getLogger(ClientThread.class.getName());
		logger.info("Constructing Server Thread...");
	}
	
	public void run() {
		try {
			while (isRunning) {
				String line = null;
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(this.in));
				PrintWriter writer = new PrintWriter(this.out);

				try {
					line = reader.readLine();
					System.out.println("Server read: " + line);
					writer.println("Input received");
					if (line.equals("quit")) {
						isRunning = false;
					}
				} catch (SocketException s) {
					// Connection reset
					// Connection closed
					// Socket write error
					isRunning = false;
					logger.info("The client did something we don't like: "
							+ s.getMessage());
				}
			}
			
			this.out.close();
			this.in.close();
			socket.close();
		} catch (IOException e) {
			logger.info("Unanticipated error, printing stack trace...");
			e.printStackTrace();
		}
	}
}
