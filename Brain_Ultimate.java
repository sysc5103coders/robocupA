import java.io.IOException;
import java.lang.Math;
import java.net.SocketException;
import java.util.regex.*;

public class Brain_Ultimate {
    
    static ActionPool actions = new ActionPool();
    static SensorPool sensors = new SensorPool();
    
    public Brain_Ultimate()
    {
        try
        {
            actions.start_pool();
            sensors.start();
        }catch(Exception ex){}
        run_brain();
    }
    
    public static void run_brain()
    {   
        String test = "";
        ObjectInfo object;
		if(Pattern.matches("^before_kick_off.*",actions.m_playmode))
			actions.move( -Math.random()*52.5 , 34 - Math.random()*68.0 );
        
        while(!sensors.m_timeOver)
        {
            object = sensors.m_memory.getObject("ball");
            if( object == null )
            {
                actions.turn(40);
                sensors.m_memory.waitForNewInfo();
            }
        }
        actions.bye();
    }
    
    /*public static void main(String a[])	
	throws SocketException, IOException
    {
        try
            {
                actions.start_pool();
                eye.start_printer();
                //sensors = SensorPool.getInstance();
                //sensors.start();
            }catch(Exception ex){}
        run_brain();
    }*/
}
