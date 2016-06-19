SOURCE=projeto

all:
	xelatex $(SOURCE).tex
	bibtex $(SOURCE)
	xelatex $(SOURCE).tex
	xelatex $(SOURCE).tex


clean:
	rm -f *.aux *.log *.idx *.ps *dvi *.pdf *.toc *.bbl *.blg *.out *.nav *.snm *.vrb
	rm -f *~
