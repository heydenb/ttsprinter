package be.tts.printer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for EntryPoint
 * 
 * @author heydenb
 */
public class EntryPointTest {

	public static final String JAVA_IO_TMPDIR = "java.io.tmpdir";
	private final static Logger LOGGER = Logger.getLogger(EntryPointTest.class.getName());
	
	private SecurityManager originalSM = null;
	
	@Before
	public void initialize(){
		originalSM = System.getSecurityManager();
    	System.setSecurityManager(new NoExitSecurityManager());
	}
	
	@After
	public void tearDown(){
		System.setSecurityManager(originalSM);
	}
	
	/**
	 * Check that system exits when incorrect parameter list is provided
	 */
    @Test (expected=ExitException.class)
	public void testMainNoParameters(){
    	EntryPoint.main(new String[]{});
	}
    
    @Test
    public void testMain(){
    	EntryPoint.main(new String[]{System.getProperty(EntryPointTest.JAVA_IO_TMPDIR)});
    	assertTrue("PathListerThread must be alive", EntryPoint.getInstance().getPathListenerThread().isAlive());
    	EntryPoint.getInstance().stop();
    	assertFalse("PathListerThread must be dead", EntryPoint.getInstance().getPathListenerThread().isAlive());
    }
	
	@Test
	public void testThreadingStuff() throws Exception{
		final List<String> verifyRuns = new ArrayList<>();
		EntryPoint ep = new EntryPoint(System.getProperty(EntryPointTest.JAVA_IO_TMPDIR)){
			
			PathListener createPathListener(String listenOnPath){
				return new PathListener(listenOnPath, new InvoiceFileProcessor() {
					
					public InvoiceData processInvoiceFile(File invFile) {
						LOGGER.info("processing called");
						verifyRuns.add("processing called");
						return null;
					}

					public boolean canProcess(File potentialInvFile) {
						return true;
					}
				}, null);
			}
			
		};
		ep.start();
		Thread.sleep(15000l);
		assertEquals(2, verifyRuns.size());
		try{
			System.exit(0);
		} catch (ExitException e){
			assertEquals(2, verifyRuns.size());
		}
	}
	
    protected static class ExitException extends SecurityException 
    {
		private static final long serialVersionUID = 1L;
		
		public final int status;
        public ExitException(int status) 
        {
            super("There is no escape!");
            this.status = status;
        }
    }

    private static class NoExitSecurityManager extends SecurityManager 
    {
        @Override
        public void checkPermission(Permission perm) 
        {
            // allow anything.
        }
        @Override
        public void checkPermission(Permission perm, Object context) 
        {
            // allow anything.
        }
        @Override
        public void checkExit(int status) 
        {
            super.checkExit(status);
            throw new ExitException(status);
        }
    }
}
