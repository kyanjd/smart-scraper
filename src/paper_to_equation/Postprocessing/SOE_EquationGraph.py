from sympy import *
from collections import defaultdict, deque
import matplotlib.pyplot as plt
import networkx as nx
import numpy as np
from tqdm import tqdm
import re

class SystemOfEquations():
    def __init__(self, equations=None, filepath=None):
        self.sympy_equations = []
        self.filepath = filepath
        self.symbol_lines = set() # Only keep one instance of each symbol line
        self.symbols = set() # Same for symbols

        if equations and filepath:
            raise Exception("Cannot have both equations and a file path.")
        if equations:
            self.str_equations = equations
        if filepath:
            self.str_equations = []
            self._extract_equations()       

        for eq in self.str_equations:
            if not self.validate(eq):
                raise Exception(f"Invalid equation: {eq}")
            else:
                self.parse_equation(eq) 

        self.ode = any(eq.has(Derivative) for eq in self.sympy_equations) # Check if any equation is an ODE

    def _extract_equations(self):
        """
        Store the equations in a list of strings
        """
        with open (f"{self.filepath}", "r") as file: 
            for line in file:
                self.str_equations.append(line)

    def get_str_equations(self):
        """
        Getter for the equation list
        """
        if not self.str_equations:
            self._extract_equations()

        return self.str_equations

    def validate(self, equation):
        """
        Check if the equation string is valid SymPy Python code

        Args:
            equation (str): The equation string to validate

        Returns:
            True if the equation is valid
            False if the equation is invalid
        """
        try:
            exec(equation) # Execute the equation string as code
            return True
        except Exception as e:
            return False
        
    def _add_underscore(self, text):
        """
        Add underscores to variable names in the equation string in case of invalid conversion
        """
        return re.sub(r'\b([A-Za-z])([A-Za-z0-9]+)(?![_\(])\b', r'\1_\2', text)

    def parse_equation(self, equation):
        """
        Parse the equation string and store the SymPy equations

        Args:
            equation (str): The equation string to parse
        """
        splitter = "\n" if "\n" in equation else "\\n" # Works for standard and repr string display
        for line in equation.split(splitter): # Split equation string into lines (e.g. Symbol, Symbol, Eq)
            if "Eq" in line:
                sympy_equation = line.split(" = ")[1]
                sympy_equation = sympy_equation.replace('"\n', "")
                sympy_equation = self._add_underscore(sympy_equation)
                self.sympy_equations.append(sympify(sympy_equation)) # Store the equation as a SymPy equation

    def get_sympy_equations(self):
        """
        Getter for the SymPy equations
        """
        return self.sympy_equations
    
    def _remove_duplicates(self, equation_number, equations):
        target_eq = equations[equation_number - 1] # 0-indexed
        target = target_eq.lhs.free_symbols # Variable to solve for
        # self.target = target

        duplicates = [] # Store indices of unwanted equations with the same target variable
        for i, eq in enumerate(equations):
            if eq.lhs.free_symbols == target:
                duplicates.append(i)
        
        result = [item for i, item in enumerate(equations) if i not in duplicates] # Remove duplicates
        result.insert(0, target_eq) # Reinsert target equation at the beginning
        # self.equations.insert(0, target_eq) # Reinsert target equation at the beginning
        return result
    
    def reduce_system(self, equation_number, const_dict):
        """
        Reduce the system of equations to those necessary to solve the target equation

        Args:  
            equation_n (int): The equation number to solve from the paper
        
        Returns:
            A list of SymPy equations that are necessary to solve the target equation
        """
        constants_symbol_dict = {Symbol(k.split(" ")[0]): v for k, v in const_dict.items()} # Convert each constant to a symbol with a value, removing the units
        constants_symbol_dict[Symbol("δ")] = 1.5e-5 # WIP hardcoded for now
        expressions = [eq.subs(constants_symbol_dict) for eq in self.sympy_equations]
        self.graph = EquationGraph(expressions)
        reduced_equations = self.graph.get_system_of_equations()
        return reduced_equations
            
    def reduce_symbols(self, const_dict):
        for eq in self.sympy_equations:
            self.symbols.add(eq.free_symbols)

        consts = set(const_dict.keys())
        consts = [const.split(" ")[0] for const in consts] # Remove the units from the constant
        self.symbols = self.symbols - set(consts)
        return self.symbols
    
    def solve_system(self, const_dict, independent_vals: list, independent_symbol: str, target_symbol: str, equation_number: int,
                     target_initial=None, t_initial=None, t_final=None, dt=None):
        """
        Solve the system of equations for the target variable
        
        Args:
            const_dict (dict): Dictionary of constants and their values
            independent_vals (list): List of values for the independent variable
            independent_symbol (str): The symbol for the independent variable
            target_symbol (str): The symbol for the target variable
            equation_number (int): The equation number to solve from the paper

        Returns:
            y_pred (list): List of predicted values for the target variable
        """
        if self.ode:
            self.solve_ode_system(const_dict, independent_vals, independent_symbol, target_symbol, equation_number,
                                  target_initial, t_initial, t_final, dt)
        else:
            self.sympy_equations = self._remove_duplicates(equation_number, self.sympy_equations)
            y_pred = []
            for x in tqdm(independent_vals, desc='Generating Curve'):
                const_dict[independent_symbol] = x
                exprs = self.reduce_system(equation_number, const_dict)
                sol = solve(exprs)[0]
                y_pred.append(sol[Symbol(target_symbol)])
            return y_pred
        
    def solve_ode_system(self, const_dict, independent_vals: list, independent_symbol: str, target_symbol: str, equation_number: int,
                         target_initial=None, t_initial=None, t_final=None, dt=None):
        self.sympy_equations = self._remove_duplicates(equation_number, self.sympy_equations)
        y_pred = []
        pass
    
    def calculate_percent_error(self, y_pred, y_true):
        y_pred = np.array(y_pred)
        y_true = np.array(y_true)
        return np.abs((y_pred - y_true) / y_true) * 100

    def plot_graph(self, x, y_pred, y_true):
        plt.plot(x, y_pred, label='Predicted')
        plt.plot(x, y_true, label='True')
        plt.legend()
        plt.show()

    def plot_error_graph(self, x, y_pred, y_true):
        error = self.calculate_percent_error(y_pred, y_true)
        plt.plot(x, error)
        plt.show()

    def plot_dependency_graph(self):
        self.graph.plot_graph()
    
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
    def __init__(self, equations):
        self.equations = equations
        self.graph = defaultdict(set) # Ensures no repeated dependencies
        self.var_equation_map = {}
        self._graph_init()

    def _graph_init(self):
        target_eq = self.equations[0] # Will always be the first equation from earlier reinsertion
        self.target = target_eq.lhs.free_symbols # Variable to solve for

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
    
    def plot_graph(self):
        """
        Visualise the dependency graph
        """
        dependencies = self.BFS_for_vars()

        G = nx.DiGraph()
        for var in dependencies:
            G.add_node(var)

        for var in dependencies:
            for dep in self.graph[var]:
                G.add_edge(var, dep) if dep in dependencies else None
        
        pos = nx.nx_agraph.graphviz_layout(G, prog="dot") # Tree layout
        plt.figure(figsize=(10, 8))
        plt.title(f"{self.target} Dependency Graph")
        nx.draw_networkx(G, pos, with_labels=True, node_size=5000, node_color="skyblue", font_size=10, font_weight="bold")
        plt.axis("off")
        plt.show()


    
    # def _solve_system(self, equations, x_vals, target):
    #     k_s = 0.14
    #     k_t = 0.0315
    #     k_l = 0.024
    #     Rs = 3.4e-07
    #     Rt = 9.6e-07
    #     h_a = 0.8
    #     σ_U = 21.0
    #     α = 0.000201
    #     λ = 6.05
    #     β = 0.00011
    #     γ = 200000.0
    #     δ = 1.5e-5

    #     k = 1
    #     p = 1
    #     H = 1
    #     θ = 1
    #     σ = 1
    #     K = 1
    #     C = 1
    #     A = 1
    #     B = 1
    #     k_f = 1
    #     k_w = 1
    #     h_f = 1
    #     h_g = 1

    #     # Variables
    #     h = Symbol("h")
    #     h_c = Symbol("h_c")
    #     K_st = Symbol("K_st")
    #     R = Symbol("R")
    #     N_P = Symbol("N_P")
    #     h_l = Symbol("h_l")
    #     K_stl = Symbol("K_stl")
    #     N_L = Symbol("N_L")
        
        
        
    #     y_pred = []
    #     for x in tqdm(x_vals, desc='Generating Curve'):
    #         P = x
    #         exprs = [Eq(h, h_a + h_c + h_l), 
    #         Eq(h_c, K_st*N_P*α/R), 
    #         Eq(K_st, 2/(1/k_t + 1/k_s)), 
    #         Eq(R, sqrt(Rs**2 + Rt**2)), 
    #         Eq(N_P, 1 - exp(-P*λ/σ_U)), 
    #         Eq(h_l, K_stl*N_L*β/R), 
    #         Eq(K_stl, 3/(1/k_t + 1/k_s + 1/k_l)), 
    #         Eq(N_L, 1 - exp(-γ*δ))]
    #         sol = solve(exprs)[0] # equations
    #         # print(sol)
    #         y_pred.append(sol[target])
    #     return y_pred