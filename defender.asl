// Agent defender in project robocupA.mas2j



/* Initial beliefs and rules */


myself(me).

/* Initial goals */



!start.



/* Plans */



+!start : true <- .print("starting defender."); 
	look.

+see(b) : near(b) <- .print("see b 1"); kick(b); -near(b); look.

+see(b) : not(near(b)) <- .print("see b 2"); turn(b); dash(b); -see(b); look.

+see(g) : near(g) <- .print("see g 1"); turn(g); dash(g); look.

+see(g) : not(near(g)) <- .print("see g 2"); turn(g); dash(g); look.

+see <- .print("defender.see nothing"); -see; look.

+see(_) <- .print("defender.see nothing (_)"); -see; look.

