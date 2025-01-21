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

    def reduce_system(self, equation_n):
        """
        Reduce the system of equations to those necessary to solve the target equation

        Args:  
            equation_n (int): The equation number to solve from the paper
        
        Returns:
            A list of SymPy equations that are necessary to solve the target equation
        """
        graph = EquationGraph(self.eq, equation_n)
        return graph.get_system_of_equations()
    
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


class EquationGraph():
    """
    Encapsulated logic for creating and managing a directed dependency graph of equations 
    (nodes = LHS variables, arcs = RHS variables dependent on LHS variables from equations)
    """
    def __init__(self, equations, equation_n):
        self.n = equation_n - 1 # 0-indexed
        self.equations = equations
        self.graph = defaultdict(set) # Ensures no repeated dependencies
        self.var_equation_map = {}
        self._graph_init()

    def _graph_init(self):
        target_eq = self.equations[self.n] 
        target = target_eq.lhs.free_symbols # Variable to solve for
        self.target = target

        duplicates = [] # Store indices of unwanted equations with the same target variable
        for i, eq in enumerate(self.equations):
            if eq.lhs.free_symbols == target:
                duplicates.append(i)
        
        result = [item for i, item in enumerate(self.equations) if i not in duplicates] # Remove duplicates
        self.equations = result
        self.equations.insert(0, target_eq) # Reinsert target equation at the beginning

        for eq in self.equations:
            lhs = eq.lhs.free_symbols
            rhs = eq.rhs.free_symbols

            for var in lhs:
                self.graph[var].update(rhs) # Add arcs from LHS to RHS variables
                self.var_equation_map[var] = eq
    
    def BFS_for_vars(self):
        """
        Breadth-first search to find all variables that the target variable depends on
        """
        seen = set() # Track visited nodes
        queue = deque(self.target) # Double-ended queue

        while queue: 
            var = queue.popleft() # Extract the leftmost element
            if var not in seen:
                seen.add(var)
                queue.extend(self.graph[var]) # Add all dependent variables to the queue and continue search
        
        return seen
    
    def get_system_of_equations(self):
        dependencies = self.BFS_for_vars() # Get all variables the target variable depends on
        return [self.var_equation_map[var] for var in dependencies] # Return the equations needed to be solved for the target variable