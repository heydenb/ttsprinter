/**
 * 
 */
package be.tts.printer;

import java.io.File;

import org.junit.Test;

/**
 * @author heydenb
 *
 */
public class InvoiceGeneratorTest {

	@Test
	public void testSome(){
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("pf_20171210_110759.txt").getFile());
		
		InvoiceFileProcessorImpl ifp = new InvoiceFileProcessorImpl();
		InvoiceData data = ifp.processInvoiceFile(file);
		data.postProcess();
		
		InvoiceGenerator ig = new InvoiceGenerator();
		ig.generateInvoice(data, new File(System.getProperty(EntryPointTest.JAVA_IO_TMPDIR)).toPath());
	}
	
}
