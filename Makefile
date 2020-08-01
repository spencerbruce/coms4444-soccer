run:
	java soccer.sim.Simulator -t 1000 --players random -s 10 -l log.txt

gui:
	java soccer.sim.Simulator -t 1000 --players random -s 10 --gui -l log.txt

compile:
	javac soccer/sim/*.java

clean:
	rm soccer/*/*.class