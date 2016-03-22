package org.gkh.net.ssl;

//import java.awt.SplashScreen;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;

import org.gkh.net.ssl.gui.SSLTesterFrame;
import org.gkh.net.ssl.gui.SplashScreen;

public class App {

	public static final ExecutorService TASKPOOL = Executors.newFixedThreadPool(2);

	public static void main(String[] args) {
		Locale.setDefault(Locale.ENGLISH);
		
		final SplashScreen ss = new SplashScreen();

        // Load and show the splash screen while we load other things.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ss.setVisible(true);
            }
        });

        // Once everything is loaded, close the splash screen.
        ss.close();
        
        new SSLTesterFrame();
	}
}
