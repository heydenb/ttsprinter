package be.tts.printer;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.*;

/**
 * Test for PathListener
 * 
 * @author heydenb
 */
public class PathListenerTest {

	@Mock
	InvoiceFileProcessor processor;
	
	@Mock
	InvoiceGenerator generator;
	
	@Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	/**
	 * ListenOn is required
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullListenOn(){
		new PathListener(null, null, null);
	}
	
	/**
	 * Processor is required
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullProcessor(){
		new PathListener("", null, null);
	}
	
	/**
	 * Bad path should break
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testBadPath(){
		new PathListener("bad", processor, null);
	}
	
	/**
	 * Test the normal run flow
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testRun() throws IOException, InterruptedException{
		File tempDir = new File(System.getProperty(EntryPointTest.JAVA_IO_TMPDIR) + "/test-" + System.currentTimeMillis());
		tempDir.mkdirs();
		File tmpFactFile = File.createTempFile("factfile", ".tmp", tempDir);
		
		when(processor.canProcess(tmpFactFile)).thenReturn(true);
		
		PathListener pl = new PathListener(tempDir.getAbsolutePath(), processor, generator);
		Thread plt = new Thread(pl);
		plt.start();
		
		Thread.sleep(1000); //give the PathListener Thread a moment to do what it does.
		
		verify(processor).canProcess(tmpFactFile);
		verify(processor).processInvoiceFile(tmpFactFile);
		
		pl.terminate();
		plt.join();
	}
	
}
