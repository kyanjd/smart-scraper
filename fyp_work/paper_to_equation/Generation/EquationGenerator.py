import pandas as pd
import numpy as np
import sympy as sp
from sympy import *
import matplotlib.pyplot as plt
import random
from IPython.display import Markdown
from lxml import etree
import json

class Equation:
    def __init__(self):
        self.latin = symbols('a b c d e f g h i j k l m n o p q r s t u v w x y z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z')
        self.greek = symbols('α β γ δ ε ζ η θ ι κ λ μ ν ξ ο π ρ σ τ υ φ χ ψ ω Α Β Γ Δ Ε Ζ Η Θ Ι Κ Λ Μ Ν Ξ Ο Π Ρ Σ Τ Υ Φ Χ Ψ Ω')
        self.vars = self.latin + self.greek 
        self.operators = ('+', '-', '*', '/', '**')
        self.functions = (sin, cos, tan, exp, log, sqrt)
        self.nums = tuple(range(1, 10))

        self.get_combined_vars(52) # 52 to roughly match the number of latin and greek letters for equal probability of choice
        self.vars += self.combined_vars # Add the combined variables to the list of variables

    def generate_expression(self):            
        complexity1 = random.randint(1, 2) # Length of expression
        expression = random.choice(self.functions)(random.choice(self.vars))  # Start with a function of a variable e.g. sin(a)
        for _ in range(complexity1):
            operator = random.choice(self.operators)
            complexity2 = random.randint(1, 3) # Complexity of the term
            if complexity2 == 1:
                term = random.choice((random.choice(self.vars), random.choice(self.nums))) # e.g. a, 1
            elif complexity2 == 2:
                term = random.choice(self.functions)(random.choice(self.vars)) # e.g. sin(a), log(b)
            elif complexity2 == 3:
                func = random.choice(self.functions)
                inner1 = random.choice(self.vars)
                inner2 = random.choice((random.choice(self.nums), random.choice(self.vars)))
                inner_operator = random.choice(self.operators)
                term = f"{func.__name__}({inner1} {inner_operator} {inner2})" # e.g. sin(a + b), log(c * d)   
            expression = f"{expression} {operator} {term}" # Concatenate the expression with the operator and term
        # print(expression)
        return sympify(expression)
    
    def get_combined_vars(self, num):
        combined_vars = []
        for _ in range(num): # Create num combined variables by joining two random variables with a "_"
            part1 = random.choice(self.vars)
            part2 = random.choice(self.vars + self.nums)
            combined_vars.append(f"{part1}_{part2}")
        self.combined_vars = symbols(" ".join(combined_vars)) # Convert the list of combined variables to a tuple of SymPy symbols
    
    def generate_equation(self):
        lhs = self.generate_expression()
        rhs = self.generate_expression()
        self.equation = Eq(lhs, rhs) # Create an attribute containing a SymPy equation in the form of lhs = rhs
    
    def to_python(self):
        self.py = sp.printing.python(self.equation) # Convert the SymPy expression to Python code
    
    def to_mathml(self):
        self.mml = sp.printing.mathml(self.equation, printer='presentation') # Convert the SymPy expression to MathML in the correct style
    
    def format_mathml(self):
        mml = self.mml
        mml = mml.replace("<mo>&InvisibleTimes;</mo>", "") # Remove invisible times operator to match scraped MathML
        mml = mml.replace("<mi>&ExponentialE;</mi>", "<mtext>exp</mtext>") # Replace exponential e with exp to match scraped MathML
        parser = etree.XMLParser(remove_blank_text=True) # Create an XML parser that removes blank text
        root = etree.fromstring(mml, parser) # Parse the MathML string into an XML element tree

        namespace = "http://www.w3.org/1998/Math/MathML" # Define the MathML namespace
        
        def add_namespace(elem): # Recursively add the namespace to all elements
            elem.tag = f"{{{namespace}}}{elem.tag}" # Add the namespace to the current tag
            for child in elem: # Do the same for all children
                add_namespace(child)                
    
        add_namespace(root) # Apply namespace

        mml =  etree.tostring(root, pretty_print=True, xml_declaration=False, encoding="UTF-8").decode("utf-8") # Convert the XML element tree back to a string
        mml = '\n'.join([line.lstrip() for line in mml.splitlines()]) # Remove leading whitespace from each line
        mml = mml.replace("ns0", "mml") # Replace the namespace prefix with "mml" to match scraped MathML
        mml = mml.replace('<mml:mrow xmlns:mml="http://www.w3.org/1998/Math/MathML">', "") # Remove first tag
        
        index = mml.rfind("</mml:mrow>") # Logic to remove final mrow tag
        if index != -1:
            mml = mml[:index] + mml[index + 11:] # 11 is len(tag)       
        
        self.mml = mml # Update attribute

    def print_latex(self):
        display(Markdown(f"$$ {latex(self.equation)} $$")) # Display the equation in LaTeX format

    def generate(self):
        self.generate_equation()
        self.to_python()
        self.to_mathml()
        self.format_mathml()
        return self.py, self.mml

class BaseDataset:
    def __init__(self, num, filepath):
        self.num = num
        self.filepath = filepath
    
    def get_columns(self):
        raise NotImplementedError("Subclasses must implement this method")
    
    def create_json(self):
        try: # Check to see if there is already data at the filepath
            with open(self.filepath, "r") as f:
                existing_data = json.load(f) # Load it if it exists
        except FileNotFoundError:
            existing_data = [] # Create an empty list otherwise

        new_data = []
        columns = self.get_columns()
        while len(new_data) < self.num: # Add num new equations to the list in dictionary format
            eg = Equation()
            try: # Skip errors
                py, mml = eg.generate()
                mml.replace("\n", "\\n")
                py.replace("\n", "\\n")
                new_data.append({columns[0]: mml, columns[1]: py}) # Format here
            except:
                continue
            print(f"Number of new equations: {len(new_data)} / {self.num}")

        
        existing_data.extend(new_data)
        with open(self.filepath, "w", encoding="utf-8") as f:
            json.dump(existing_data, f, ensure_ascii=False, indent=4)

    def create_csv(self):
        try: # Check to see if there is already data at the filepath
            existing_data = pd.read_csv(self.filepath) # Load it if it exists
        except FileNotFoundError:
            existing_data = pd.DataFrame(columns=["text_input", "output"]) # Create an empty df otherwise

        new_data = []
        while len(new_data) < self.num: # Add num new equations to the list in dictionary format
            eg = Equation()
            try: # Skip errors
                py, mml = eg.generate()
                new_data.append({"text_input": mml, "output": py}) # Format here
            except:
                continue
            print(f"Number of new equations: {len(new_data)} / {self.num}")
        
        new_data = pd.DataFrame(new_data)
        existing_data = pd.concat([existing_data, new_data])
        existing_data.to_csv(self.filepath, index=False)

    def create(self):
        if self.filepath.split(".")[-1] == "json":
            self.create_json()
        elif self.filepath.split(".")[-1] == "csv":
            self.create_csv()
        else:
            print("Invalid file format. Please use .json or .csv")