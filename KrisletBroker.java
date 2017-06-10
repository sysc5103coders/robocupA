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

    public boolean executeAction(String agName, Structure action) {
		if (!krisMap.containsKey(agName)) newKrislet(agName, getRunAction(agName, action));
		getRunAction(agName, action).apply(krisMap.get(agName));
		return true;
    }
	
	public Function getRunAction(String agName, Structure action) {
		return (krislet_) -> {
			Krislet krislet = (Krislet) krislet_;
			krisMap.put(agName, krislet);
			logger.info("runAction: "+action+", for agent=" + agName + ", krislet=" + krislet);
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
		String id = "ball".equals(type)? 
			"ball": "goal " + krisMap.get(agName).getBrain().getSide();
		if (krisMap.get(agName)!=null && krisMap.get(agName).getBrain()!=null &&
			krisMap.get(agName).getBrain().getMemory()!=null)
			return krisMap.get(agName).getBrain().getMemory().getObject(id);
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


