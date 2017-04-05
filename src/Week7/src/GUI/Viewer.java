package Week7.src.GUI;

import Week7.src.Utils.Position;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Shows the map of Horst with the current location drawn on it
 * @author Bernd
 *
 */
public class Viewer implements Runnable {

	private Set<int[]> points;
	private LinkedBlockingQueue<Position> pos;
	
	private static double MAX_X = 200;
	private static double MAX_Y = 50;
	private static double X_START = 44;
	private static double X_END = 1764;
	private static double Y_START = 450;
	private static double Y_END = 20;
	
	private static double Y_SCALING = (Y_START - Y_END)/MAX_Y;
	private static double X_SCALING = (X_END - X_START)/MAX_X;
	
	public Viewer(LinkedBlockingQueue<Position> pos){
		this.pos = pos;
	}

	@Override
	public void run() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(1792, 485);
		Image image;
		Image backup;
		Image point;
		try {
			image = ImageIO.read(new File("OHorst.png")); //ImageIO.read(new File("spiegel_beter.png"));
			backup = ImageIO.read(new File("OHorst.png")); //ImageIO.read(new File("spiegel_beter.png"));
			point = ImageIO.read(new File("point.png"));
			JLabel label = new JLabel(new ImageIcon(image));
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(label);
			frame.add(panel);
			frame.setVisible(true);
			
			Graphics draw = image.getGraphics();
			draw.setColor(Color.RED);

			Position currentPos;
			while (true) {
				currentPos = pos.take();
				currentPos = getMapPosition(currentPos);
				draw.drawImage(backup, 0, 0, null);
				draw.drawImage(point, (int) currentPos.getX() - 5, (int) currentPos.getY() - 5, null);
				label.repaint();

				for (int[] p : points) {
					Position ppos = getMapPosition(new Position(p[0], p[1]));
					int radius = (p[2] * 10);
					draw.setColor(new Color(p[3], p[4], p[5]));
					draw.drawOval((int) (ppos.getX() - radius / 2) - 5, (int) (ppos.getY() - radius / 2) - 5, radius, radius);
					draw.setColor(new Color(p[3], p[4], p[5], 120));
					draw.fillOval((int) (ppos.getX() - radius / 2) - 5, (int) (ppos.getY() - radius / 2) - 5, radius, radius);
				}
			}

		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updatePoints(final HashSet<int[]> update) {
		SwingUtilities.invokeLater(() -> points = update);
	}



	/**
	 * Translates from map grid coordinates to pixel coordinates used for drawing
	 * @param pos
	 * @return
	 */
	public static Position getMapPosition(Position pos){
		double x = pos.getX();
		x = x * X_SCALING;
		x = x + X_START;
		
		double y = pos.getY();
		y = ( MAX_Y - y ) * Y_SCALING;
		y = y + Y_END;
		
		return new Position(x,y);
	}
}
