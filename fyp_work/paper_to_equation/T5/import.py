import sys
print(sys.path)
# import numpy
# sys.path.append(r"C:\Users\kyanj\Documents\Repos\smart-scraper")
import fyp_work.paper_to_equation.generation.EquationGenerator as eg

eq = eg.Equation()
py, mml = eq.generate()
print(py)
print(mml)
