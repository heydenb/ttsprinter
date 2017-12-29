package be.tts.printer;

import java.io.File;

import org.junit.Test;

/**
 * Test for FactFileProcessorImpl
 * 
 * @author heydenb
 *
 */
public class InvoiceFileProcessorImplTest {

	/**
	 * Test the data extraction from the invoice files
	 */
	@Test
	public void testProcess(){
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("pf_20171210_110759.txt").getFile());
		
		InvoiceFileProcessorImpl ifp = new InvoiceFileProcessorImpl();
		InvoiceData data = ifp.processInvoiceFile(file);
		
		//TODO: verify
		System.out.println(data.toString());
	}
	
}
