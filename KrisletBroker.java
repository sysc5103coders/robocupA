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
		//logger.info("executeAction, agent name: " + agName);
		if (!krisMap.containsKey(agName)) newKrislet(agName, getRunAction(agName, action));
		getRunAction(agName, action).apply(krisMap.get(agName));
		return true;
    }
	
	public Function getRunAction(String agName, Structure action) {
		//logger.info("getRunAction, agentName: " + agName + " action: " + action.getFunctor());
		return (krislet_) -> {
			Krislet krislet = (Krislet) krislet_;
			
			if (action.getFunctor().equals("look")) { 
				//logger.info("found look");
				checkEnv(agName);
			} 
			else if (action.getFunctor().equals("turn"))
			{
				logger.info("action: turn");
				krislet.turn(40.0);	
				clearPercepts(agName);
			}
			else if (action.getFunctor().equals("runtowardsball"))
			{
				//logger.info("action: runtowardsball");
				ObjectInfo obj = getObject(agName, "ball");
				if(null != obj)
				{
					if(obj.m_direction != 0)
						krislet.turn(obj.m_direction);
					else
						krislet.dash(10*obj.m_distance);
				}
				else
					krislet.turn(40.0);
				clearPercepts(agName);
			} 
			else if(action.getFunctor().equals("kicktowardsgoal"))
			{
				logger.info("action: kicktowardsgoal");
				ObjectInfo obj = getObject(agName, "goal");
				if (null != obj)
					krislet.kick(100, obj.m_direction);
				else
					krislet.turn(40.0);
				clearPercepts(agName);
			}
			else if(action.getFunctor().equals("kicktowardsplayer"))
			{
				logger.info("action: kicktowardsplayer");
				ObjectInfo obj = getObject(agName, "player");
				if (null != obj)
					krislet.kick(100, obj.m_direction);	
				else
					krislet.turn(40.0);
				clearPercepts(agName);			
			}
			else
			{
				logger.info("action: unknown");
				krislet.turn(40.0);
				clearPercepts(agName);
			}
			/*
			else if (action.getFunctor().equals("turn")) { 
				ObjectInfo obj = getObject(agName, "ball");
				obj = obj==null? getObject(agName, "goal"): obj;
				krislet.turn(obj==null? 27.1: obj.m_direction);
			} else if (action.getFunctor().equals("dash")) { 
				krislet.dash(1000);
			} else if (action.getFunctor().equals("kick")) { 
				ObjectInfo ball = getObject(agName, "goal");
				krislet.kick(1000, ball==null? 27.1: ball.m_direction);
			}
			*/
			pause();
			return true; // the action was executed with success 
		};
	}

	
	private ObjectInfo getObject(String agName, String type) {
		//logger.info("getObject, agent name: " + agName + " object type: " +type);
		if (krisMap.get(agName)!=null && krisMap.get(agName).getBrain()!=null &&
		    krisMap.get(agName).getBrain().getMemory()!=null) 
		{
			String id = "";
			switch(type)
			{
				case "ball":
					id = "ball";
					break;
				case "player":
					id = "player";
					break;
				case "goal":
					id = "goal " + (krisMap.get(agName).getBrain().getSide() == 'l' ? "r" : "l");
					logger.info("the goal string: " + id);
					break;
				default:
					break;
			}
			return krisMap.get(agName).getBrain().getMemory().getObject(id);
		}
		logger.info("Could not get Memory instance.");
		return null;
	}
	
	private void checkEnv(String agName) {
		logger.info("check env.");
		ObjectInfo b = getObject(agName, "ball");
	    ObjectInfo g;
	    ObjectInfo paux = getObject(agName, "player");
	    PlayerInfo p;
		
		//seeBall and haveBall
		boolean bTeamMateAvailable = false;
		boolean bHaveBall = false;
		boolean bSeeBall = false;
		if(b!=null)
		{
			if(b.m_distance<1.0)
			{
				logger.info("ball dist less than 1");
				addPercept(agName, Literal.parseLiteral("haveball(b)"));
				bHaveBall = true;
			}
			else
			{
				//logger.info("ball dist more than 1");
				addPercept(agName, Literal.parseLiteral("seeball(b)"));
				bSeeBall = true;
			}
		}
			

		if(paux!=null)
		{
			p = (PlayerInfo)paux;
			if (p != null)
			{
				if(team.equals(p.m_teamName))
				{
					//addPercept(Literal.parseLiteral("team(p)"));
					bTeamMateAvailable = true;
					if(closestToTheBall(b, paux) && bSeeBall)
					{
						addPercept(agName, Literal.parseLiteral("closest(b)"));
						logger.info("closest to the ball");
					}
				}
			}
		}
		if(!bTeamMateAvailable)
			addPercept(agName, Literal.parseLiteral("closest(b)"));
			
		if(bHaveBall)
		{
			//Close to goal.
			g = getObject(agName, "goal");
			if(g!=null)
			{
				addPercept(agName, Literal.parseLiteral("seegoal(g)"));
				logger.info("have ball and goal is visible");
				if (g.m_distance<=16)
					addPercept(agName, Literal.parseLiteral("closetogoal(g)"));
				else
				{				
					if (!bTeamMateAvailable || closestToTheGoal(g,paux))
					{
						logger.info("have ball, goal visible and closest to it");
						addPercept(agName, Literal.parseLiteral("closest(g)"));
					}
					else
						logger.info("not closest to goal");
				}
			}
			else{logger.info("not seeing the goal");}
		}

		addPercept(agName, Literal.parseLiteral("see(_)"));
    }
 
	private void pause() {
		//logger.info("function pause");
		try {
	        Thread.sleep(2*SoccerParams.simulator_step);
	    } catch (Exception e){
			logger.info("pause exception");
		}
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
			
		double ballToOtherPlayerDistance = getDistance(ball.m_distance, ball.m_direction, otherPlayer.m_distance, otherPlayer.m_direction)+3;
		
		return ball.m_distance < ballToOtherPlayerDistance;
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
			
		double goalToOtherPlayerDistance = getDistance(goal.m_distance, goal.m_direction, otherPlayer.m_distance, otherPlayer.m_direction)+3;
		
		return goal.m_distance < goalToOtherPlayerDistance;	
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


