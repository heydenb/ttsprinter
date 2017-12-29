package be.tts.printer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author heydenb
 *
 */
public class InvoiceGenerator {

	/**
	 * 
	 * @param data
	 */
	public File generateInvoice(InvoiceData data, Path target){
		InputStream jasperInput = this.getClass().getResourceAsStream("/tts-invoice.jasper");
		File out = new File(target.toString() + File.separator + constructName((String)data.getData().get("FACTNR")));
		try {
			JasperPrint jprint = (JasperPrint) JasperFillManager.fillReport(jasperInput, data.getData(), data);
			try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(out))){
				JasperExportManager.exportReportToPdfStream(jprint, bos);
			}
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
	}

	private String constructName(String factNummer) {
		return factNummer.replace("/", "_") + "_" + System.currentTimeMillis() + ".pdf";
	}
}
