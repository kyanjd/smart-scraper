# **Smart Scraper**

This repository is for the FYP (Final Year Project) of Kyan Jasani-Draper involving the automated development of ready-to-use functional modules for tribology from journal articles.

To-Do (n = priority / 5):
- review usage of repr vs left aligned new lines vs pretty print new lines for MML representation in LLM input and training data (3)
- review and standardise file types, class inputs and outputs, etc. (4)
- ~~develop dependency graph for finding the equations neededed to solve a top level equation (2)~~
- fix the solve rhs metric for imaginary equation solutions (2)
- extract constants from paper, must be done on campus wifi (2)
- finish SOE class for recreating python file results (3)
- create utils class to abstract file I/O methods (1)
- sort out returns vs modifying internal attributes (1)

Process:
- input a DOI referring to a tribology paper
- extract constants from tables and save to a file
- extract MathML representations of equations and save to a separate file
- pass MathML equations to a LLM for conversion to individual SymPy equations and save to a file
- pass the collection of SymPy equations along with the constants to a separate LLM for conversion to a system of equations ready for solving
- implement the system of equations back into Python for solving, plotting and error analysis
