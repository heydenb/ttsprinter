package be.tts.printer;

import java.io.File;

/**
 * @author heydenb
 *
 */
public interface InvoiceFileProcessor {

	/**
	 * 
	 * @param factFile
	 */
	InvoiceData processInvoiceFile(File invoiceFile);

	/**
	 * 
	 * @param potentialFactFile
	 * @return
	 */
	boolean canProcess(File potentialInvoiceFile);
	
}
