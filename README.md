# Studio Planner

A Constraint Satisfaction Solver to plan music studio playing session.

Domain : Time slots x Studios
Values : Song to play by players
Constraint : 
	- Certain song must be played on specific studio
	- One player at time except "free" one

Remark1 : Time slot are dynamics (solving problem in only one pass, no backtracking, but constraint a rough strategy to give the best solution first)
Remark2 : Algorithm can be backtracked manualy to give next solution

## Screenshot

![Planner screenshot](/screenshotPlanning.png?raw=true "Screenshot")

