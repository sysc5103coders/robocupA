// Environment code for project robocupA.mas2j



import jason.asSyntax.*;

import jason.environment.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.Properties;



public class KrisletBroker extends Environment {

    private Logger logger = Logger.getLogger("robocupA.mas2j."+KrisletBroker.class.getName());
	
	private Map<String, Krislet> krisMap = new HashMap();
	
	private String host = "", team = "Team";

	private int port = 6000;


    /** Called before the MAS execution with the args informed in .mas2j */

    @Override

    public void init(String[] args) {
        super.init(args);
		
		Properties props = new Properties();
		try {
			FileInputStream in = new FileInputStream("robocupA.properties");
			props.load(in);
			host = props.getProperty("host");
			port = Integer.valueOf(props.getProperty("port"));
			team = props.getProperty("team");
		} catch (Exception e) { }
    }



    @Override

    public boolean executeAction(String agName, Structure action) {
		if (!krisMap.containsKey(agName)) newKrislet(agName, getRunAction(agName, action));
		getRunAction(agName, action).apply(krisMap.get(agName));
		return true;
    }
	
	public Function getRunAction(String agName, Structure action) {
		return (krislet_) -> {
			Krislet krislet = (Krislet) krislet_;
			//krisMap.put(agName, krislet);
			if (action.getFunctor().equals("look")) { 
				logger.info("found look");
				checkEnv(agName);
			} else if (action.getFunctor().equals("turn")) { 
				ObjectInfo obj = getObject(agName, "ball");
				obj = obj==null? getObject(agName, "goal"): obj;
				krislet.turn(obj==null? 27.1: obj.m_direction);
			} else if (action.getFunctor().equals("dash")) { 
				krislet.dash(1000);
			} else if (action.getFunctor().equals("kick")) { 
				ObjectInfo ball = getObject(agName, "goal");
				krislet.kick(1000, ball==null? 27.1: ball.m_direction);
			}
			pause();
			return true; // the action was executed with success 
		};
	}

	
	private ObjectInfo getObject(String agName, String type) {
		if (krisMap.get(agName)!=null && krisMap.get(agName).getBrain()!=null &&
		    krisMap.get(agName).getBrain().getMemory()!=null) {
			String id = "ball".equals(type)? 
				"ball": "goal " + krisMap.get(agName).getBrain().getSide(); 
			return krisMap.get(agName).getBrain().getMemory().getObject(id);
		}
		logger.info("Could not get Memory instance.");
		return null;
	}
	
	private void checkEnv(String agName) {
		ObjectInfo b = getObject(agName, "ball");
	    ObjectInfo g = getObject(agName, "goal");
		logger.info("checkEnv." + agName + ", b=" + b + ", g=" + g);
		if ((b != null || g != null) && (b==null? g: b).m_distance < 0.1)
			addPercept(Literal.parseLiteral(b==null? "+near(g)": "+near(b)"));
		if (b != null) { logger.info("saw ball"); addPercept(Literal.parseLiteral("see(b)")); }
		else if (g != null) addPercept(Literal.parseLiteral("see(g)"));
		else { logger.info("saw nothing"); addPercept(Literal.parseLiteral("see")); }
		addPercept(Literal.parseLiteral("see(_)"));
    }
 
	private void pause() {
		try {
	        Thread.sleep(2*SoccerParams.simulator_step);
	    } catch (Exception e){}
	}
	
	private void newKrislet(String agName, Function fn) {
		Krislet krislet;
		try {
		    krislet = new Krislet(InetAddress.getByName(host), port, team, "", fn);
			krisMap.put(agName, krislet);
			krislet.initialize();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}


    /** Called before the end of MAS execution */

    @Override

    public void stop() {
	
        super.stop();

    }
	
	// returns true if the calling player is closer to the ball than 'otherPlayer' is
	// This should happen but in can it does, returns false if ball is null
	// Returns true if otherPlayer is null
	private boolean closestToTheBall(ObjectInfo ball, ObjectInfo otherPlayer)
	{
		if(null == ball)
			return false;
		if(null == otherPlayer)
			return true;
			
		double ballToOtherPlayerDistance = getDistance(ball.m_distance, ball.m_direction, otherPlayer.m_distance, otherPlayer.m_direction);
		
		return ball.m_distance <= ballToOtherPlayerDistance;
	}

	// returns true if the calling player is closer to the goal than 'otherPlayer' is
	// This should happen but in can it does, returns false if goal is null
	// Returns true if otherPlayer is null
	private boolean closestToTheGoal(ObjectInfo goal, ObjectInfo otherPlayer)
	{
		if(null == goal)
			return false;
		if(null == otherPlayer)
			return true;
			
		double goalToOtherPlayerDistance = getDistance(goal.m_distance, goal.m_direction, otherPlayer.m_distance, otherPlayer.m_direction);
		
		return goal.m_distance <= goalToOtherPlayerDistance;	
	}
	
	// dDist and dDir represent the distance and and angle of objects 1 and 2 wrt to the same object considered to be at coordinate (0,0)
	private double getDistance(double dDist1, double dDir1, double dDist2, double dDir2)
	{
		//compute coordinates for object 1
		double dX1, dY1;
        dY1 = Math.cos(Math.toRadians(dDir1)) * dDist1;
        dX1 = Math.sin(Math.toRadians(dDir1)) * dDist1;
        
		// compute coordinates for object 2
        double dX2, dY2;
        dY2 = Math.cos(Math.toRadians(dDir2)) * dDist2;
        dX2 = Math.sin(Math.toRadians(dDir2)) * dDist2;
        
		// return the euclidean distance between object 1 and 2
        return Math.sqrt(Math.pow((dX1 - dX2), 2.0) + Math.pow((dY1 - dY2), 2.0));
	}

}


