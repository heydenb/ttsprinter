package be.tts.printer;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * This is the starting class containing the main method
 * 
 * @author heydenb
 */
public class EntryPoint {

	private final static Logger LOGGER;
	static {
		InputStream stream = EntryPoint.class.getClassLoader().getResourceAsStream("logging.properties");
		try {
			LogManager.getLogManager().readConfiguration(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		LOGGER = Logger.getLogger(EntryPoint.class.getName());
	}

	private static EntryPoint _instance = null;
	private Thread pathListenerThread = null;
	private PathListener pathListener = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("USAGE: please provide the path attribute.");
			System.exit(1);
		}
		String listenOnPath = args[0];

		if (_instance == null){
			_instance = new EntryPoint(listenOnPath);
			_instance.start();
		}
	}

	/**
	 * @return the EntryPoint instance
	 */
	public static EntryPoint getInstance(){
		return _instance;
	}
	
	/**
	 * 
	 * @param listenOnPath
	 */
	public EntryPoint(String listenOnPath) {
		LOGGER.info("EntryPoint launched");
		pathListener = createPathListener(listenOnPath);
		pathListenerThread = new Thread(pathListener);
		pathListenerThread.setDaemon(false);
	}

	/* package */ PathListener createPathListener(String listenOnPath) {
		return new PathListener(listenOnPath, new InvoiceFileProcessorImpl(), new InvoiceGenerator());
	}

	/* package */ void start() {
		LOGGER.fine("start called");
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			public void run() {
				stop();
			}

		}));
		pathListenerThread.start();
	}

	/* package */ void stop() {
		System.out.println("stop called");
		pathListener.terminate();
		try {
			pathListenerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/* package */ Thread getPathListenerThread(){
		return pathListenerThread;
	}

}
