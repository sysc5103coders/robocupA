// Agent dummy in project robocupA.mas2j



/* Initial beliefs and rules */


myself(me).
/* Initial goals */



!start.



/* Plans */



+!start : true <- .print("starting dummy2..."); look.
+see(b) : haveball(b) & not seegoal(g) <- .print("have ball but not seeing goal"); turn; look.
+see(b) : haveball(b) & closetogoal(g) <- .print("have ball, close to goal..."); kicktowardsgoal(g); look.
+see(b) : haveball(b) & closest(g) <- .print("have ball, not close to goal, but closest..."); kicktowardsgoal(g); look.
+see(b) : haveball(b) & not closest(g) & not closetogoal(g) <- .print("have ball, not close to goal nor closest.."); 
																kicktowardsplayer(p); look.	
+see(b) : seeball(b) & closest(b) <- .print("Saw ball, closest..."); runtowardsball(b); look.
+see(b) : seeball(b) & not closest(b) <- .print("Saw ball, not closest..."); runslowlytowardsball(b); look.
									
+see(_) <- .print("dummy.see nothing (_)"); turn; look.


