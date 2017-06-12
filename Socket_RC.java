import java.net.*;

public class Socket_RC {
    
    public DatagramSocket m_socket;    // Socket to communicate with server
    private static Socket_RC socket_rc = new Socket_RC();
    
    protected Socket_RC(){
        try{    
            m_socket = new DatagramSocket();
        }catch(Exception ex){}
    }
    
    public void finalize()
    {
		m_socket.close();
    }
    
    public static Socket_RC getInstance(){
        return socket_rc;
    }
    
}
