
all:
	mkassdocs2.pl > doit.sh
	bash doit.sh
	mv out/*.pdf .
	ls *.pdf

test:
	rm -fr out
	mkassdocs2.pl $(HOME)/teambin/testrecords.csv > doit.sh
	bash doit.sh	

clean:
	rm -fr out doit.sh *.log *.aux *.out



