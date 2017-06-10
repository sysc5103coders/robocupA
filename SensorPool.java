import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SensorPool extends Thread implements SensorInput {
    
    public  Boolean          m_playing = true;
    public  Memory           m_memory = new Memory();
    private Socket_RC        socket_rc;
    private DatagramSocket   m_socket;
    volatile public boolean  m_timeOver;
    int MSG_SIZE = 4096;
    private Pattern message_pattern = Pattern.compile("^\\((\\w+?)\\s.*");
    private Pattern hear_pattern = Pattern.compile("^\\(hear\\s(\\w+?)\\s(\\w+?)\\s(.*)\\).*");
    private static SensorPool pool = new SensorPool();

//Constructor & Destructor
//===========================================================================    
    protected SensorPool() 
    {
        try{
            socket_rc = Socket_RC.getInstance(); 
            m_socket = socket_rc.m_socket;
        }catch(Exception e){}
    }
    public void finalize()
    {
	socket_rc.finalize();
    }
//===========================================================================        
    
//Main functions of class.
//=========================================================================== 
    @Override
    public void run()
    {
        try{
        while( m_playing )
            parseSensorInformation(receive());
        }catch(Exception ex){}
        finalize();
    }
    // This function parses sensor information
    private void parseSensorInformation(String message)
	throws IOException
    {
	Matcher m=message_pattern.matcher(message);
	if(!m.matches())
	    {
		throw new IOException(message);
	    }
	if( m.group(1).compareTo("see") == 0 )
	    {
		VisualInfo	info = new VisualInfo(message);
		info.parse();
		this.see(info);
	    }
	else if( m.group(1).compareTo("hear") == 0 )
	    parseHear(message);
    }
    // This function parses hear information
    private void parseHear(String message)
	throws IOException
    {
	Matcher m=hear_pattern.matcher(message);
	int	time;
	String sender;
	String uttered;
	if(!m.matches())
	    {
		throw new IOException(message);
	    }
	time = Integer.parseInt(m.group(1));
	sender = m.group(2);
	uttered = m.group(3);
	if( sender.compareTo("referee") == 0 )
	    this.hear(time,uttered);
	else if( sender.compareTo("self") != 0 )
	    this.hear(time,Integer.parseInt(sender),uttered);
    }
    // This function waits for new message from server
    private String receive() 
    {
	byte[] buffer = new byte[MSG_SIZE];
	DatagramPacket packet = new DatagramPacket(buffer, MSG_SIZE);
	try{
	    m_socket.receive(packet);
	}catch(SocketException e){ 
	    System.out.println("shutting down...");
	}catch(IOException e){
	    System.err.println("socket receiving error " + e);
	}
	return new String(buffer);
    }
//===========================================================================
    
//Sense commands.
//===========================================================================
    @Override
    public void see(VisualInfo info) {
        m_memory.store(info);
    }
    @Override
    public void hear(int time, String message) {
        if(message.compareTo("time_over") == 0)
	    m_timeOver = true;
    }
    @Override
    public void hear(int time, int direction, String message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
//===========================================================================
    
//Aux methods
//===========================================================================    
    public static SensorPool getInstance(){
        return pool;
    }
    public static String returnString()
    {
        return "lol";
    }
//===========================================================================
    
}
