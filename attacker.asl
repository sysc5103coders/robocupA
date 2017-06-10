// Agent attacker in project robocupA.mas2j



/* Initial beliefs and rules */


myself(me).

/* Initial goals */



!start.



/* Plans */



+!start : true <- .print("starting attacker."); 
	look.
	
+see <- .print("attacker.see nothing").

+see(b) : near(b) <- kick(b); -near(b).

+see(b) : not(near(b)) <- turn(b); dash(b).

+see(g) : near(g) <- turn(g); dash(g).

+see(g) : not(near(g)) <- turn(g); dash(g).

