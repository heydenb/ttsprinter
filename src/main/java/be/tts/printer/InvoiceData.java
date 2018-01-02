package be.tts.printer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 * 
 * @author heydenb
 */
public class InvoiceData implements JRDataSource{

	private static SimpleDateFormat FACT_DATE_RAW = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat FACT_DATE_REPORT = new SimpleDateFormat("d MMMM yyyy", new Locale("nl", "BE"));
	
	private Map<String, Object> data;
	private List<List<String>> details;
	private List<String> detailHeaders;
	private int pointer = -1;
	
	public InvoiceData(){}
	
	/**
	 * Used by the designer
	 * @return sample data
	 */
	public static JRDataSource getDataSource(){
		InvoiceData id = new InvoiceData();
		id.populateDetails(Arrays.asList("REGELNRx","OMSCHRIJVINGx","ITEMBEDRAG1x","ITEMBEDRAG2x"), Arrays.asList(Arrays.asList("1","TUINONDERHOUD","0.00","838.84")));
		return id;
	}
	
	/**
	 * Create new invoice data object and return it.
	 * @return
	 */
	public Map<String, Object> createInvoiceData(){
		data = new HashMap<>();
		return data;
	}
	
	public Map<String, Object> getData() {
		return data;
	}

	/**
	 * 
	 * @param detailHeaders
	 * @param details
	 */
	public void populateDetails(List<String> detailHeaders, List<List<String>> details) {
		this.detailHeaders = detailHeaders;
		this.details = details;
	}

	/**
	 * Post processing handles some data type conversions and some conveniences for the report.
	 */
	public void postProcess(){
		if (StringUtils.isEmpty((String)data.get("AANSPREEK"))){
			data.put("AANSPREEK_NAAM", (String)data.get("NAAM"));
		}
		else{
			data.put("AANSPREEK_NAAM", (String)data.get("AANSPREEK") + " " + (String)data.get("NAAM"));
		}
		try {
			Date factDatRaw = FACT_DATE_RAW.parse((String)data.get("FACTDATUM"));
			data.put("FACTDATUM", FACT_DATE_REPORT.format(factDatRaw));
		} catch (ParseException e) {
			//Keep the date for what it is.
		}
		if (!StringUtils.isEmpty((String)data.get("BTWNUMMER"))){
			if ("BE-".equals((String)data.get("BTWNUMMER"))){
				data.put("BTWNUMMER", "");
			}
			else{
				data.put("BTWNUMMER", ((String)data.get("BTWNUMMER")).replace("BE-", "BE 0"));
			}
		}
		convertPropToDouble("BTWBASIS1");
		convertPropToDouble("BTWBASIS2");
		convertPropToDouble("BTWBEDRAG1");
		convertPropToDouble("BTWBEDRAG2");
		convertPropToDouble("FACTUURBEDRAG");
	}
	
	/**
	 * Covert a parameter to Double for the report
	 * @param propName
	 */
	private void convertPropToDouble(String propName){
		Double v = Double.valueOf((String)data.get(propName));
		if (v == 0.0){
			v = null;
		}
		data.put(propName, v);
	}
	
	/**
	 * @return true when the Object is completely populated
	 */
	public boolean isReady(){
		return data!=null 
				&& data.size()>10 
				&& details!=null 
				&& !details.isEmpty() 
				&& detailHeaders!=null 
				&& detailHeaders.size()==4;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.jasperreports.engine.JRDataSource#next()
	 */
	@Override
	public boolean next() throws JRException {
		pointer++;
		if (details.size() <= pointer && pointer > 10){
			return false;
		}
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.jasperreports.engine.JRDataSource#getFieldValue(net.sf.jasperreports.engine.JRField)
	 */
	@Override
	public Object getFieldValue(JRField jrField) throws JRException {
		if (details.size() <= pointer){
			return null;
		}
		String value = details.get(pointer).get(detailHeaders.indexOf(jrField.getName()));
		if (jrField.getName().startsWith("ITEMBEDRAG")){
			Double dv = Double.valueOf(value);
			if (dv==0.0){
				return null;
			}
			return dv;
		}
		return value;
	}

	@Override
	public String toString() {
		return data + "\n" + detailHeaders + "\n" + details;
	}
}
