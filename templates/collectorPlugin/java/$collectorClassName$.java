package $packageName$;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.hq.product.Collector;
import org.hyperic.hq.product.PluginException;

/**
 * 
 *
 */
public class $collectorClassName$ extends Collector {

	/** Used socket for connection. */
	private Socket socket = null;
	
	/** Used port for connection. */
    private int port = -1;
    
    /** Default port. */
    private static final int DEFAULT_PORT = 80;
    
    /** Reader for connection. */
    private BufferedReader reader = null;
    
    /** Writer for connection. */
    private BufferedWriter writer = null;
    
    /** Constant for unix style line break. */
    public static final String LF = "\n";
    
    /** Constant for win style line break. */
    public static final String CRLF = "\r\n";
    
    /** Constant for mac style line break. */
    public static final String CR = "\r";

    /** Property key for line break type. */
    public static final String PROP_NEWLINE = "newline";
    
    /** Property key for line to be written to connection. */
    public static final String PROP_STRING_TO_WRITE = "stringtowrite";
    
    /** Property key for expected string. */
    public static final String PROP_STRING_EXPECT = "stringexpect";

    /** Key for storing value for response waiting time. */
    public static final String ATTR_RESPONSE_WAITING_TIME = "ResponseWaitingTime";

    /** Key for storing value for connection waiting time. */
    public static final String ATTR_CONNECTION_WAITING_TIME = "ConnectionWaitingTime";
    
    /** Line termination. */
    private String newLine = null;
    
    /** String to write to connection. */
    private String toWrite = null;
    
    /** Expected result from connection. */
    private String stringExpect = null;

    // these denotes timestamps for events
    private long conStartTime, writeStartTime, gotResponseTime;
    
    /** Logging instance. */
    private Log log = LogFactory.getLog($collectorClassName$.class.getName());


	/**
	 * 
	 * @see org.hyperic.hq.product.Collector#collect()
	 */
	public void collect() {
        try {
        	conStartTime = System.currentTimeMillis();
            connect();
            
            writeStartTime = System.currentTimeMillis();
            writeLine(toWrite);
            
            String line = readResponse();
            gotResponseTime = System.currentTimeMillis();
            
            setAvailability(checkExpected(line));
            
	        setValue(ATTR_RESPONSE_WAITING_TIME, gotResponseTime - writeStartTime);     
	        setValue(ATTR_CONNECTION_WAITING_TIME, writeStartTime - conStartTime);     
		} catch (IOException e) {
		} finally {
			close();
		}
	}
	
	/**
	 * This function is called by super implementation after
	 * plugin config has been initialized. We can now
	 * collect our own settings.
	 * 
	 * @see org.hyperic.hq.product.Collector#init()
	 */
    protected void init() throws PluginException {
    	
        Properties props = getProperties();
        newLine = props.getProperty(PROP_NEWLINE);
        toWrite = props.getProperty(PROP_STRING_TO_WRITE);
        stringExpect = props.getProperty(PROP_STRING_EXPECT);
    }

	/**
	 * Check if pattern is matching.
	 * 
	 * @param line to match against.
	 * @return true if pattern matches
	 */
	private boolean checkExpected(String line) {
		
		// if there's no string to match or it's empty, just match.
		if(stringExpect == null || stringExpect.trim().length() < 1)
			return true;
		
		// match by regex
		Pattern p = Pattern.compile(stringExpect);
		Matcher m = p.matcher(line);
		return m.find();
	}

	/**
	 * Writes line to connection.
	 * 
	 * @param line line to write.
	 * @throws IOException
	 */
	private void writeLine(String line) throws IOException {
        getWriter();
        this.writer.write(line);
        this.writer.write(getLineTermination());
        this.writer.flush();
    }
    
	/**
	 * Finds matching line termination string.
	 * 
	 * @return Line termination.
	 */
    private String getLineTermination() {
    	if(newLine.equals("CR"))
    		return CR;
    	else if(newLine.equals("CRLF"))
    		return CRLF;
    	else if(newLine.equals("LF"))
    		return LF;
    	else
    		return CRLF;
    }

    /**
     * Reads response from connection.
     *  
     * @return First line read from connection.
     * @throws IOException
     */
    protected String readResponse() throws IOException {
        getReader();
        return this.reader.readLine();
    }


	/**
	 * Connects to socket.
	 * 
	 * @throws IOException
	 */
	protected void connect() throws IOException {
		socket = new Socket();
		InetSocketAddress saddr = getSocketAddress();
		try {
			socket.connect(saddr, getTimeoutMillis());
			socket.setSoTimeout(getTimeoutMillis());
			setMessage("OK");
		} catch (IOException e) {
			setMessage("connect " + saddr, e);
			throw e;
		}        
	}
	
	/**
	 * Closing socket connection.
	 */
	protected void close() {
        if (socket != null) {
            try {
				socket.close();
			} catch (IOException e) {
			}
        }

	}

	/**
	 * Helper method to create Inet Socket Address.
	 * @return InetSocketAddress
	 */
	protected InetSocketAddress getSocketAddress() {
        String host = getHostname();
        int port = getPort();
        if (getSource() == null) {
            setSource(host + ":" + port);
        }
        InetSocketAddress saddr =
            new InetSocketAddress(host, port);
        return saddr;
    }
    
	/**
	 * Finds hostname from properties.
	 * @return hostname
	 */
	protected String getHostname() {
    	return getProperties().getProperty(PROP_HOSTNAME, DEFAULT_HOSTNAME);
    }
    
	/**
	 * Finds port from properties.
	 * @return port
	 */
	protected int getPort() {
        if (this.port == -1)
            this.port = getIntegerProperty(PROP_PORT, DEFAULT_PORT);    
        return this.port;
    }

	/**
	 * Helper method to get reader.
	 * @param socket
	 * @return
	 * @throws IOException
	 */
	public static BufferedReader getReader(Socket socket) throws IOException {
		InputStreamReader is = new InputStreamReader(socket.getInputStream());
		return new BufferedReader(is);
	}

	/**
	 * Helper method to get writer.
	 * 
	 * @param socket
	 * @return
	 * @throws IOException
	 */
	public static BufferedWriter getWriter(Socket socket) throws IOException {
		OutputStreamWriter os = new OutputStreamWriter(socket.getOutputStream());
		return new BufferedWriter(os);
	}

	/**
	 * Helper method to get reader.
	 * 
	 * @return
	 * @throws IOException
	 */
    protected BufferedReader getReader() throws IOException {
        if (this.reader == null) {
            this.reader = getReader(this.socket);
        }
        return this.reader;
    }
    
    /**
	 * Helper method to get writer.
     * 
     * @return
     * @throws IOException
     */
    protected BufferedWriter getWriter() throws IOException {
        if (this.writer == null) {
            this.writer = getWriter(this.socket);
        }
        return this.writer;
    }

}
