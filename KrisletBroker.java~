// Environment code for project robocupA.mas2j



import jason.asSyntax.*;

import jason.environment.*;

import java.net.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;



public class KrisletBroker extends Environment {



    private Logger logger = Logger.getLogger("robocupA.mas2j."+KrisletBroker.class.getName());
	
	private Map<String, Krislet> krisMap = new HashMap();



    /** Called before the MAS execution with the args informed in .mas2j */

    @Override

    public void init(String[] args) {

        super.init(args);

        /*addPercept(ASSyntax.parseLiteral("percept(demo)"));
*/	
    }



    @Override

    public boolean executeAction(String agName, Structure action) 
    {
		if (!krisMap.containsKey(agName)) 
			newKrislet(agName, getRunAction(agName, action));
		getRunAction(agName, action).apply(krisMap.get(agName));
		return true;
    }
	
	public Function getRunAction(String agName, Structure action) 
	{
		return (krislet_) -> {
			Krislet krislet = (Krislet) krislet_;
			logger.info("runAction: "+action+", for agent=" + agName + ", krislet=" + krislet);
			
			if (action.getFunctor().equals("look")) 
			{ 
				look(krislet);
				//logger.info("found look");
				//lookFor();
				//checkEnv(agName);
			} else if (action.getFunctor().equals("turn")) { 
				turn(krislet);
				//ObjectInfo obj = getObject(agName, "ball");
				//obj = obj==null? getObject(agName, "goal"): obj;
				//krislet.turn(obj==null? 27.1: obj.m_direction);
			} else if (action.getFunctor().equals("runTowardsBall")) { 
				runTowardsBall(krislet);
				//krislet.dash(1000);
			} else if (action.getFunctor().equals("kickTowardsGoal")) { 
				kickTowards(krislet,"goal");
				//ObjectInfo ball = getObject(agName, "goal");
				//krislet.kick(1000, ball==null? 27.1: ball.m_direction);
			} else if (action.getFunctor().equals("kickTowardsPlayer")) { 
				kickTowards(krislet,"player");
				//ObjectInfo ball = getObject(agName, "goal");
				//krislet.kick(1000, ball==null? 27.1: ball.m_direction);
			}
			pause();
			return true; // the action was executed with success 
		};
	}

	//Set Of actions
	//=============================================================
	public void runTowardsBall(Krislet krislet)
	{
		ObjectInfo ball = krislet.getBrain().getMemory().getObject("ball");
		if(ball!=null)
		{
			krislet.turn(ball.m_direction);
			krislet.dash(10*ball.m_distance);
		}
	}
	public void kickTowards(Krislet krislet, String object)
	{
		switch(object)
		{
			case "goal":
				ObjectInfo goal;
				if( krislet.getBrain().m_side == 'l' )
				    goal = krislet.getBrain().getMemory().getObject("goal r");
				else
				    goal = krislet.getBrain().getMemory().getObject("goal l");
				krislet.kick(100,goal.m_direction);
				break;
			case "player":
				PlayerInfo object_player;
				if( krislet.getBrain().m_side == 'l' )
				    goal = krislet.getBrain().getMemory().getObject("goal r");
				else
				    goal = krislet.getBrain().getMemory().getObject("goal l");
				krislet.kick(100,goal.m_direction);
				break;
			default:
				break;
		}
	}
	public void look(Krislet krislet)
	{
		checkEnv(krislet);	
	}
	public void turn(Krislet krislet)
	{
		krislet.turn(40);
	}

	//=============================================================
	
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
	
	private void checkEnv(Krislet krislet) {
		ObjectInfo b = krislet.getBrain().getMemory().getObject("ball");
	    ObjectInfo g;
	    ObjectInfo paux = krislet.getBrain().getMemory().getObject("player");
	    PlayerInfo p;
		//seeBall and haveBall
		if(b!=null)
			if(b.m_distance<1.0)
				addPercept(Literal.parseLiteral("haveBall(b)"));
			else
			{
				addPercept(Literal.parseLiteral("seeBall(b)"));
				//addPercept(Literal.parseLiteral("not haveBall(b)"));
			}
		else
			addPercept(Literal.parseLiteral("turnForBall(g,b)"));
		//seeGoal
		if( krislet.getBrain().m_side == 'l' )
		    g = krislet.getBrain().getMemory().getObject("goal r");
		else
		    g = krislet.getBrain().getMemory().getObject("goal l");
		if(g!=null)
			addPercept(Literal.parseLiteral("seeGoal(g)"));
		//else
			//addPercept(Literal.parseLiteral("not seeGoal(g)"));
		//closest and team(p)
	    if(paux!=null)
	    {
	    	p = (PlayerInfo)paux;
			if (p != null)
				if((krislet.getBrain().m_team).equals(p.m_teamName))
				{
					addPercept(Literal.parseLiteral("team(p)"));
		            if(p.m_distance-b.m_distance>=b.m_distance)
		                addPercept(Literal.parseLiteral("closest(b)"));
				}
		}
		//else
			//addPercept(Literal.parseLiteral("not team(p)"));
		//Close to goal.
		if(g!=null)
			if (g.m_distance<=10)
				addPercept(Literal.parseLiteral("closeToGoal(g)"));
			//else
			//	addPercept(Literal.parseLiteral("not closeToGoal(g)"));	
    }
 
	private void pause() {
		try {
	        Thread.sleep(2*SoccerParams.simulator_step);
	    } catch (Exception e){}
	}
	
	private void newKrislet(String agName, Function fn) {
		Krislet krislet;
		try {
		    krislet = new Krislet(InetAddress.getByName(""), 6000, "TeamA", "", fn);
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

}


