from sympy import *
from collections import defaultdict, deque

class SystemOfEquations():
    def __init__(self, filepath):
        self.str_equations = []
        self.eq = []
        self.filepath = filepath
        self.symbol_lines = set() # Only keep one instance of each symbol line
        self.symbols = set() # Same for symbols
        self._extract_equations()
        for eq in self.str_equations:
            if not self.validate(eq):
                raise Exception("MathML to SymPy conversion error.") 
        

    def _extract_equations(self):
        """
        Store the equations in a list of strings
        """
        with open (f"{self.filepath}", "r") as file: 
            for line in file:
                self.str_equations.append(line)

    def get_equations(self):
        """
        Getter for the equation list
        """
        if not self.str_equations:
            self.extract_equations()

        return self.str_equations

    def validate(self, equation):
        """
        Check if the equation string is valid SymPy/Python code

        Args:
            equation (str): The equation string to validate

        Returns:
            True if the equation is valid
            False if the equation is invalid
        """
        try:
            eval(equation) # Execute the equation string as code
            return True
        except Exception as e:
            print(e)
            return False

    def parse_equation(self, equation):
        """
        Parse the equation string and store the symbols and equations

        Args:
            equation (str): The equation string to parse
        """
        for line in equation.split("\\n"): # Split equation string into lines (e.g. Symbol, Symbol, Eq)
            if "Symbol" in line:
                line = line.replace('"', "")
                self.symbol_lines.add(line) # Store line for file creation
                self.symbols.add(line.split(" = ")[0]) # Store symbol as a variable
            elif "Eq" in line:
                eq = line.split(" = ")[1]
                eq = eq.replace('"\n', "")
                self.eq.append(sympify(eq)) # Store the equation as a SymPy equation

    def solve(self, target):
        pass
    
    def solve_test(self, target):
        symbol_list = {"a", "b", "c"}
        const = {"c"}

        # Define symbols dynamically
        sd = {name: Symbol(name) for name in symbol_list if name not in const}

        # Substitute c = 4 directly in the equations
        c = 4
        # og_eq = [Eq(a, b + 5), Eq(b, c + 1)]
        equations = [Eq(sd["a"], sd["b"] + 5), Eq(sd["b"], c + 1)]
        # equations = [eq.subs({a: sd["a"], b: sd["b"]}) for eq in og_eq]

        # Solve for a
        result = solve(equations)
        print(result[sd[target]])
    
    def solve_test2(self, target):
        pass


class EquationGraph():
    def __init__(self, equations, equation_n):
        self.n = equation_n - 1
        self.equations = equations
        self.graph = defaultdict(set)
        self.var_equation = {}
        self._graph_init()

    def _graph_init(self):
        target_eq = self.equations[self.n]
        target = target_eq.lhs.free_symbols

        duplicates = []
        for i, eq in enumerate(self.equations):
            if eq.lhs.free_symbols == target:
                duplicates.append(i)

        result = [item for i, item in enumerate(self.equations) if i not in duplicates]
        self.equations = result
        self.equations.insert(0, target_eq)
        self.target = target

        for eq in self.equations:
            lhs = eq.lhs.free_symbols
            rhs = eq.rhs.free_symbols

            for var in lhs:
                self.graph[var].update(rhs)
                self.var_equation[var] = eq
    
    def BFS_for_vars(self):
        seen = set()
        queue = deque(self.target)

        while queue:
            var = queue.popleft()
            if var not in seen:
                seen.add(var)
                queue.extend(self.graph[var])
        
        return seen
    
    def get_system_of_equations(self):
        dependencies = self.BFS_for_vars()
        return [self.var_equation[var] for var in dependencies]