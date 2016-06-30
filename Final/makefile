SOURCE=andamento

all:
	xelatex -synctex=1 $(SOURCE).tex
	bibtex $(SOURCE)
	xelatex -synctex=1 $(SOURCE).tex
	xelatex -synctex=1 $(SOURCE).tex


clean:
	rm -f *.aux *.log *.idx *.ps *dvi *.pdf *.toc *.bbl *.blg *.out *.nav *.snm *.vrb
	rm -f *~
