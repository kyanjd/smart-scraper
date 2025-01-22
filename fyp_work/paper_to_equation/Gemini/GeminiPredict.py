import google.generativeai as genai
from google.generativeai.types import GenerationConfig
import os
import csv
import sacrebleu
from tqdm import tqdm


class GeminiPredict():
    def __init__(self, api_key, filepath, model_name):
        self.predictions = []
        self.filepath = filepath
        self.filename = os.path.splitext(filepath)[0]
        self.model_name = model_name

        if api_key:
            self.api_key = api_key
            genai.configure(api_key=api_key)
            print("API key loaded successfully!")
        else:
            raise ValueError("API key not found. Please provide a valid API key.")

    def predict(self, mml, temperature=0.1):
        prompt = f"""
        You are an expert at translating MathML to Python code using the SymPy library. Translate the following MathML expression into a valid Python SymPy expression. 
        Ensure that all variables are treated as symbolic variables and each is defined separately on a new line. 
        Assume that SymPy has been imported as `from sympy import *` so DO NOT include any import statements in your output.
        Example: 
        MathML: '<mml:mi>p</mml:mi>\n<mml:mo>=</mml:mo>\n<mml:mrow>\n<mml:msub>\n<mml:mi>p</mml:mi>\n<mml:mi>a</mml:mi>\n</mml:msub>\n<mml:mo>-</mml:mo>\n<mml:msub>\n<mml:mi>y</mml:mi>\n<mml:mrow>\n<mml:mi>r</mml:mi>\n<mml:mi>e</mml:mi>\n<mml:mi>2</mml:mi>\n</mml:mrow>\n</mml:msub>\n</mml:mrow>'
        SymPy Python code: 'p = Symbols('p')\np_a = Symbols('p_a')\ny_re2 = Symbols('y_re2')\ne = Eq(p, p_a - y_re2)'
        
        Now, translate the following MathML expression into Python code using SymPy.
        MathML: {mml}
        SymPy Python code:
        """
        model = genai.GenerativeModel(model_name=self.model_name)
        generation_config = GenerationConfig(temperature=temperature)
        response = model.generate_content(prompt, generation_config=generation_config)
        return response.text

    def predict_from_txt(self):  
        with open(self.filepath, "r") as file:
            total_rows = sum(1 for row in file) # For progress bar

        with open(self.filepath, "r") as file:
            lines = file.readlines()
            with tqdm(total=total_rows, desc="Generating Predictions", unit="row") as pbar:
                for mml in lines: # Predict rows 1 by 1
                    text = self.predict(mml)
                    self.predictions.append(repr(text))
                    pbar.update(1)

    def predict_from_csv(self):
        with open(self.filepath, "r") as file:
            reader = csv.reader(file)
            total_rows = sum(1 for row in reader) - 1 # For progress bar
        
        with open(self.filepath, "r") as file:
            reader = csv.reader(file)
            next(reader)
            with tqdm(total=total_rows, desc="Generating Predictions", unit="row") as pbar:
                for row in reader:
                    mml = row[0]
                    text = self.predict(mml)
                    self.predictions.append(repr(text))
                    pbar.update(1)
                    
    def save_predictions(self, new_filename=None):
        if not self.predictions:
            print("Generating predictions first")
            self.predict_from_file()
        if new_filename is None:
            new_filename = self.filename

        with open(f"{new_filename}_predictions.txt", "w") as file:
            for pred in self.predictions:
                file.write(pred + "\n")

    def evaluate_bleu(self, filename=None, model_name=None):
        if filename is None:
            filename = self.filename
        if model_name is None:
            model_name = self.model_name
        if not self.predictions:
            # self.predict_from_file(filename, model_name)
            with open(f"{filename}.csv", "r") as file:
                reader = csv.reader(file)
                total_rows = sum(1 for row in reader) - 1 # For progress bar
            
            predictions = []  
            with open(f"{filename}.csv", "r") as file:
                reader = csv.reader(file)
                next(reader)
                with tqdm(total=total_rows, desc="Generating Predictions", unit="row") as pbar:
                    for row in reader:
                        mml = repr(row[0])
                        text = self.predict(mml, model_name=model_name)
                        predictions.append(repr(text))
                        pbar.update(1)
                    
            with open(f"{filename}_predictions.txt", "w") as file:
                for pred in predictions:
                    file.write(pred + "\n")

        with open(f"{filename}.csv", "r") as file:
            reader = csv.reader(file)
            next(reader)
            refs = [repr(row[1]) for row in reader]
        with open(f"{filename}_predictions.txt", "r") as file:
            preds = [row for row in file]

        bleu = sacrebleu.corpus_bleu(preds, [refs])
        print(f"SacreBLEU Score: {bleu.score}")

def main():
    api_key = os.getenv("GEMINI_API_KEY")
    gemini = GeminiPredict(api_key)
    gemini.evaluate_bleu("gemini_test_1", "models/gemini-1.5-flash")

if __name__ == "__main__":
    main()