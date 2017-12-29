package be.tts.printer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author heydenb
 *
 */
public class InvoiceFileProcessorImpl implements InvoiceFileProcessor {

	private static final String SEPARATOR = "\\|";
	private Charset charset = Charset.forName("ISO-8859-1");
	
	/* (non-Javadoc)
	 * @see be.tts.printer.FactFileProcessor#canProcess(java.io.File)
	 */
	public boolean canProcess(File potentialInvoiceFile) {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see be.tts.printer.FactFileProcessor#processFactFile(java.io.File)
	 */
	public InvoiceData processInvoiceFile(File invoiceFile) {
		InvoiceData data = new InvoiceData();
		Map<String, Object> invoice = data.createInvoiceData();
		List<List<String>> invoiceLines;
		try {
			try(BufferedReader br=Files.newBufferedReader(invoiceFile.toPath(), charset)) {
			    List<String> headers=Arrays.asList(br.readLine().split(SEPARATOR));
			    String[] invoiceValues = br.readLine().split(SEPARATOR);
			    for(int i=0 ; i < invoiceValues.length ; i++){
			    	invoice.put(headers.get(i), invoiceValues[i]);
			    }
			    invoiceLines = br.lines()
			    	.filter(line -> !line.startsWith("END|") && line.length()>1)
			        .map(line -> Arrays.asList(line.split(SEPARATOR)))
			        .collect(Collectors.toList());
			    data.populateDetails(headers.subList(invoiceValues.length, headers.size()-1), invoiceLines);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}	

}
