package Week7.src;

import Week7.src.GUI.Viewer;
import Week7.src.Location.AdvancedLocationFinder;
import Week7.src.Location.LocationFinder;
import Week7.src.Network.DataReceiver;
import Week7.src.Network.WebSender;
import Week7.src.Utils.MacRssiPair;
import Week7.src.Utils.Position;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Main class that starts the WlanScanner application
 * @author Bernd
 *
 */
public class WlanScanner {
	public static Viewer GUI;

	//The port the Wlan scanner server is running on. (Default = 2548)
	public static int port = 2548;

	//The ip address of the machine the Wlan scanner server is running on. (Default = 127.0.0.1 , your own machine)
	public static String targetIP = "127.0.0.1";

	//Location of the receive script for position on the website
	public static String webSenderTarget = "130.89.13.124";

	//Your group name on the website, don't forget to change this!
	public static String name = "TheMoose";

	//Controls if the position is send to the server for viewing on the website.
	public static boolean sendToWebsite = true;


	//The LocationFinder that is to be used. Replace this with your own implementation!
	public LocationFinder locationHandler;

	///////////// Do not edit below this line! ////////////////

	private LinkedBlockingQueue<MacRssiPair[]> data;
	private LinkedBlockingQueue<Position> pos;
	private LinkedBlockingQueue<Position> sendPos;

	public static void main(String[] args){
		new WlanScanner();
	}

	/**
	 * Starts a thread for communications with the data server
	 * Starts a thread for displaying the location on a map
	 * Handles data passing between the 'communications thread' -> 'locationFinder' -> 'display thread'
	 */
	public WlanScanner(){
		data = new LinkedBlockingQueue<MacRssiPair[]>(); // Queue between communicator thread and this thread
		pos = new LinkedBlockingQueue<Position>(); // Queue between this thread and the viewer thread
		sendPos = new LinkedBlockingQueue<Position>(); // Queue between this thread and the WebSender thread
		GUI = new Viewer(pos);
		Thread viewer = new Thread(GUI);
		locationHandler = new AdvancedLocationFinder(GUI);
		viewer.start();

		if(sendToWebsite){
			Thread sender = new Thread(new WebSender(webSenderTarget, sendPos, name));
			sender.start();
		}

		Thread communicator = new Thread(new DataReceiver(targetIP, port, data));
		communicator.start();

		Position tempPos;
		while(true){

			try {
				tempPos = locationHandler.locate(data.take());
				pos.add(tempPos);
				if(sendToWebsite) sendPos.add(tempPos);
				System.out.println(tempPos); //When data is available give to the locationFinder
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}