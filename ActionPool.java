import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ActionPool implements SendCommand {
    
    private Socket_RC           socket_rc;
    private DatagramSocket	m_socket;		// Socket to communicate with server
    private InetAddress		m_host;			// Server address
    private int			m_port;         	// server port
    public String		m_team;           	// team name
    public boolean             m_playing;              // controls the MainLoop
    public char                m_side;                 // Side of the feel for player,
    public int                 m_number;               // Number of player.
    public String              m_playmode;             //Current state of game.
    int	MSG_SIZE = 4096;                        // Size of socket buffer
    
//Constructor & Destructor
//===========================================================================    
    public ActionPool() 
    {
        try{
			socket_rc = Socket_RC.getInstance(); 
			m_socket = socket_rc.m_socket;
			m_host = InetAddress.getByName("");
			m_port = 6000;
			m_team = "Tigres";
			m_playing = true;
        }catch(Exception e){}
    }
    public void finalize()
    {
	socket_rc.finalize();
    }
//===========================================================================
    
//Main functions of class.
//===========================================================================
    public void start_pool() 
        throws IOException
    {
		byte[] buffer = new byte[MSG_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, MSG_SIZE);
		// first we need to initialize connection with server
		init();
		m_socket.receive(packet);
		parseInitCommand(new String(buffer));
		m_port = packet.getPort();
    }
    private void init()
    {
		send("(init " + m_team + " (version 9))");
    }
    protected void parseInitCommand(String message)
	throws IOException
    {
		Matcher m = Pattern.compile("^\\(init\\s(\\w)\\s(\\d{1,2})\\s(\\w+?)\\).*$").matcher(message);
		if(!m.matches())
			{
				throw new IOException(message);
			}
			m_side = m.group(1).charAt(0);
			m_number = Integer.parseInt(m.group(2));
			m_playmode = m.group(3);
    }
    private void send(String message)
    {
		byte[] buffer = Arrays.copyOf(message.getBytes(),MSG_SIZE);
		try{
			DatagramPacket packet = new DatagramPacket(buffer, MSG_SIZE, m_host, m_port);
			m_socket.send(packet);
		}
		catch(IOException e){
			System.err.println("socket sending error " + e);
		}
    }
//===========================================================================
    
//Commands
//=========================================================================== 
    @Override
    public void move(double x, double y)
    {
        send("(move " + Double.toString(x) + " " + Double.toString(y) + ")");
    }
    @Override
    public void turn(double moment)
    {
        send("(turn " + Double.toString(moment) + ")");
    }
    @Override
    public void turn_neck(double moment)
    {
        send("(turn_neck " + Double.toString(moment) + ")");
    }
    @Override
    public void dash(double power)
    {
        send("(dash " + Double.toString(power) + ")");
    }
    @Override
    public void kick(double power, double direction)
    {
        send("(kick " + Double.toString(power) + " " + Double.toString(direction) + ")");
    }
    @Override
    public void say(String message)
    {
        send("(say " + message + ")");
    }
    @Override
    public void changeView(String angle, String quality)
    {
        send("(change_view " + angle + " " + quality + ")");
    }
    @Override
    public void bye()
    {
        m_playing = false;
        send("(bye)");
    }
//===========================================================================
    
}
