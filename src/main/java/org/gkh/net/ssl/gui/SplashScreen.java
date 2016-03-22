package org.gkh.net.ssl.gui;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JWindow;

public class SplashScreen extends JWindow {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7005541942881521922L;

	//private static final BufferedImage img = Utils.getImage("SplashScreen");
    private final ContextMenu CONTEXT_MENU = new ContextMenu();

	public SplashScreen() {
		this.setLayout(null);
		this.setLocationRelativeTo(null);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    CONTEXT_MENU.show(SplashScreen.this, e.getX(), e.getY());
                }
            }
        });
        this.setAlwaysOnTop(false);
	}
	
	@Override
    public void paint(Graphics g) {
        //g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), null);
    }

    /**
     * Closes and disposes of the splash screen.
     */
    public void close() {
        this.setVisible(false);
        this.dispose();
    }
	
	/**
     * The context menu which is shows on right click for the splash screen image, giving a force quit option.
     */
    private final class ContextMenu extends JPopupMenu {
        /**
		 * 
		 */
		private static final long serialVersionUID = -8731525936440669390L;
		private final JMenuItem FORCE_QUIT = new JMenuItem(/*Language.INSTANCE.localize("common.forcequit")*/);

        public ContextMenu() {
            super();

            this.FORCE_QUIT.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            this.add(this.FORCE_QUIT);
        }
    }
}
