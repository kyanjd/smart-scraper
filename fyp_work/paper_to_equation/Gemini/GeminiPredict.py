import google.generativeai as genai
from google.generativeai.types import GenerationConfig
import os
import csv
import sacrebleu
from tqdm import tqdm


class GeminiPredict():
    def __init__(self, api_key, filename, model_name):
        self.api_key = api_key
        self.predictions = []
        self.filename = filename
        self.model_name = model_name

        key = os.environ.get(api_key)
        if key:
            print("API key loaded successfully!")
        else:
            print(f"API key not found. Please ensure it is set as an environment variable named {api_key}.")
        genai.configure(api_key=key)

    def predict_from_file(self, model_name=None, filename=None):  
        if model_name is None:
            model_name = self.model_name        
        if filename is None:
            filename = self.filename

        self.model_name = model_name

        with open(f"{filename}.txt", "r") as file:
            total_rows = sum(1 for row in file) # For progress bar

        with open(f"{filename}.txt", "r") as file:
            reader = csv.reader(file)
            with tqdm(total=total_rows, desc="Generating Predictions", unit="row") as pbar:
                for row in reader: # Predict rows 1 by 1
                    mml = repr(row) # Repr for consistency with training data
                    prompt = f"""
                    You are an expert at translating MathML to Python code using the SymPy library. Translate the following MathML expression into a valid Python SymPy expression. 
                    Ensure that all variables are treated as symbolic variables and each is defined separately on a new line. 
                    Assume that SymPy has been imported as `from sympy import *` so DO NOT include any import statements in your output.
                    Example: 
                    MathML: '<mml:mi>h</mml:mi>\n<mml:mo>=</mml:mo>\n<mml:mrow>\n<mml:msub>\n<mml:mi>h</mml:mi>\n<mml:mi>c</mml:mi>\n</mml:msub>\n<mml:mo>+</mml:mo>\n<mml:msub>\n<mml:mi>h</mml:mi>\n<mml:mrow>\n<mml:mi>g</mml:mi>\n<mml:mi>a</mml:mi>\n<mml:mi>t</mml:mi>\n</mml:mrow>\n</mml:msub>\n</mml:mrow>'
                    SymPy Python code: 'h = Symbols('h')\nh_gat = Symbols('h_gat')\nh_c = Symbols('h_c')\ne = Eq(h, h_gat + h_c)'
                    
                    Now, translate the following MathML expression into Python code using SymPy.
                    MathML: {mml}
                    SymPy Python code:
                    """
                    
                    model = genai.GenerativeModel(model_name=model_name)
                    generation_config = GenerationConfig(temperature=0.1)
                    response = model.generate_content(prompt, generation_config=generation_config)
                    text = response.text
                    self.predictions.append(repr(text))
                    pbar.update(1)

    def save_predictions(self):
        with open(f"{self.filename}_predictions.txt", "w") as file:
            for pred in self.predictions:
                file.write(pred + "\n")

    def evaluate_bleu(self, filename=None, model_name=None):
        if filename is None:
            filename = self.filename
        if model_name is None:
            model_name = self.model

        self.predict_from_file(filename, model_name)

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
    for key, value in os.environ.items():
        print(f"{key}: {value}")

if __name__ == "__main__":
    main()