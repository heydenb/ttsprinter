package be.tts.printer;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.Test;
import static org.mockito.Mockito.*;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 * Test for InvoiceData
 * 
 * @author heydenb
 * 
 */
public class InvoiceDataTest {

	/**
	 * testing the population of the Invoice data object
	 * testing walking this object as DataSource
	 * @throws JRException 
	 */
	@Test
	public void testPopulateAndTraversing() throws JRException{
		InvoiceData data = new InvoiceData();
		assertFalse("data object should not yet be ready", data.isReady());
		
		Map<String, Object> invoice = data.createInvoiceData();
		assertFalse("data object should not yet be ready", data.isReady());
		
		IntStream.range(0, 11).forEach(
			nbr -> invoice.put("Bogus" + nbr, "Whatever")
		);
		assertFalse("data object should not yet be ready", data.isReady());
		
		data.populateDetails(Arrays.asList("H1","H2","H3","H4"), Arrays.asList(Arrays.asList("V1","V2","V3","V4")));
		assertTrue("data object should now be ready", data.isReady());
		
		//What would Jasper do?
		assertTrue("move to first record should be true" , data.next());
		IntStream.range(1, 4).forEach(
			nbr -> checkFieldValue(data, "H"+nbr, "V"+nbr)
		);
		assertFalse("move to first record should be true" , data.next());
		
	}

	private void checkFieldValue(InvoiceData data, String header, String expectedValue) {
		JRField field = mock(JRField.class);
		when(field.getName()).thenReturn(header);
		try {
			assertEquals(expectedValue, data.getFieldValue(field));
		} catch (JRException e) {
			throw new RuntimeException(e);
		}
	}
	
}
