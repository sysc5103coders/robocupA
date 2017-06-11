// Agent defender in project robocupA.mas2j
/* Initial beliefs and rules */

myself(me).

/* Initial goals */

!start.

/* Plans */

+!start : true <- look.

+!dashWhenClose(b,p) : seeBall(b) & team(p) & closest(b) <- runTowardsBall(b); -seeBall(b); -team(p); -closest(b); look.
+!dashWhenAlone(b) : seeBall(b) <- runTowardsBall(b); -seeBall(b); look.
+!kickToGoal(g,p,b) : haveBall(b) & seeGoal(g) & closeToGoal(g) <- kickTowardsGoal(g); -haveBall(b); -seeGoal(g); -closeToGoal(g); look.
+!kickToPlayer(g,p,b) :	haveBall(b) & seeGoal(g) <-kickTowardsPlayer(p); -haveBall(b); -seeGoal(g); look.
+!turnForBall(g,b) : not(seeBall(b))  & not(haveBall(b))  <- turn; look.
+!turnForGoal(b) : haveBall(b) & not(seeGoal(g)) <- turn; look.
