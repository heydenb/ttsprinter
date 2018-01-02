package be.tts.printer;

import static org.junit.Assert.*;

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
		
		System.out.println(data.toString());
		
		assertNotNull(data);
		assertEquals("BINNENWEG 199", data.getData().get("ADRES"));
		assertTrue(data.isReady());
	}
	
	/**
	 * Testing the post processing
	 */
	@Test
	public void testPostProcess(){
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("pf_20090713_084518.txt").getFile());
		
		InvoiceFileProcessorImpl ifp = new InvoiceFileProcessorImpl();
		InvoiceData data = ifp.processInvoiceFile(file);
		
		assertNotNull(data);
		assertEquals("BE-859.635.774", data.getData().get("BTWNUMMER"));
		
		data.postProcess();
		
		assertNotNull(data);
		assertEquals("BE 0859.635.774", data.getData().get("BTWNUMMER"));
	}
}
