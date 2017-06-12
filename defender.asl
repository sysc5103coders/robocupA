// Agent defender in project robocupA.mas2j

/* Initial beliefs and rules */

myself(me).

/* Initial goals */

!start.

/* Plans */

+!start : true <- look.

+!dashwhenclose(b,p) : see(b) & team(p) & closesttoball(b) <- .print("dash to ball") ; dash(b).
+!kicktogoal(b,g,p) : haveball(b) & see(g) & closesttogoal(g) <- .print("kick to goal") ; kicktowardsthegoal(g).
+!kicktoplayer(b,g,p) : haveball(b) & see(g) & closesttoball(b) <- .print("pass the ball") ; pass(p).

+!turnforgoal(b,g) : haveball(b) & not(see(g)) <- .print("turn to goal") ; turntogoal(g).
+!turnforball(b) : not(see(b)) <- .print("turn to ball") ; turntoball(b).

