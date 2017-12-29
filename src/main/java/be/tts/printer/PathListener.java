package be.tts.printer;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

import org.castor.core.util.Assert;

/**
 * Listener that regularly (every 10 seconds) for new files on a given Path to
 * pass to a given Processor
 * 
 * @author heydenb
 */
public class PathListener implements Runnable {

	private static final int WAIT_INTERVAL_MILLIS = 10000; // 10 seconds
	private final static Logger LOGGER = Logger.getLogger(PathListener.class.getName());
	
	private static final String ARCHIVE_DIR = "archive";
	private static final String OUTPUT_DIR = "output";
	
	private volatile boolean running = true;

	private File listenOn = null;
	private File archiveDir = null;
	private File pdfOutputDir = null;
	
	private InvoiceFileProcessor processor = null;
	private InvoiceGenerator generator = null;

	/**
	 * Constructor requires the listenOn path as String and FactFileProcessor
	 * implementation
	 * 
	 * @param listenOn
	 *            the file system path which is translated to a @File
	 * @param processor
	 *            the @FactFileProcessor implementation
	 */
	public PathListener(String listenOn, InvoiceFileProcessor processor, InvoiceGenerator generator) {
		Assert.notNull(listenOn, "listenOn should not be null");
		Assert.notNull(processor, "processor should not be null");
		Assert.notNull(generator, "generator should not be null");

		this.listenOn = new File(listenOn);
		if (!this.listenOn.isDirectory()) {
			throw new IllegalArgumentException(listenOn + " is not a valid directory");
		}
		this.processor = processor;
		this.generator = generator;
		
		archiveDir = new File(this.listenOn.getAbsolutePath() + File.separator + ARCHIVE_DIR);
		archiveDir.mkdirs();
		pdfOutputDir = new File(this.listenOn.getAbsolutePath() + File.separator + OUTPUT_DIR);
		pdfOutputDir.mkdirs();
	}

	/**
	 * Clean stop the PathListener Thread
	 */
	public void terminate() {
		running = false;
	}

	/**
	 * Path Listener Processing
	 */
	public void run() {
		while (running) {
			File[] matches = listenOn.listFiles(new FileFilter(){

				/**
				 * Only files should be returned
				 */
				@Override
				public boolean accept(File file) {
					return file.isFile();
				}
				
			});
			if (matches.length > 0) {
				if (processor.canProcess(matches[0])) {
					InvoiceData invoiceData = processor.processInvoiceFile(matches[0]);
					invoiceData.postProcess();
					generator.generateInvoice(invoiceData, pdfOutputDir.toPath());
					//print the pdf.
				}
				// now move this file
				try {
					Files.move(matches[0].toPath(), constructTargetPath(listenOn, matches[0]),
							StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					LOGGER.severe(e.getMessage());
				}
			}
			
			try {
				Thread.sleep(WAIT_INTERVAL_MILLIS);
			} catch (InterruptedException e) {
				LOGGER.severe(e.getMessage());
				running = false;
			}
		}
	}

	/**
	 * construct target archive directory to move processed files
	 * 
	 * @param listenOn
	 * @param source
	 * @return archive dir Path
	 */
	private Path constructTargetPath(File listenOn, File source) {
		return new File(archiveDir.getAbsolutePath() + File.separator
				+ source.getName() + System.currentTimeMillis()).toPath();
	}

}
