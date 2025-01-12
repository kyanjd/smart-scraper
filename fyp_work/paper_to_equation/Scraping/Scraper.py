import requests
import bs4
import os

class Scraper:
    def __init__(self, doi, api_key):
        self.doi = doi
        self.api_key = api_key
        self.dataset = []

    def make_request(self):
        response = requests.get(f"https://api.elsevier.com/content/article/doi/{self.doi}?APIKey={self.api_key}") # Make request to Elsevier API
        if response.status_code == 200:
            print("Request successful \n")
            self.full_text = response.text 
        else:
            raise Exception(f"Request failed with status code: {response.status_code} \n")

    def make_soup(self):
        self.soup = bs4.BeautifulSoup(self.full_text, "lxml") # Parse response text and create a BS4 object for easy parsing

    def find_equations(self):
        self.equation = self.soup.find_all("ce:formula")

        self.mathml_dict = {}
        for eq in self.equation:
            label = eq.find("ce:label") # Find equation number in the format (n)
            index = int(label.text[1:-1]) # Remove parentheses and convert to int
            mathml = eq.find("mml:math") 
            mathml = mathml.contents[1] # Remove \n from beginning and end along with outer tags
            self.mathml_dict[index] = mathml 
    
    def create_mathml_txt(self, filename):
        mmld = self.mathml_dict
        with open(f"{filename}.txt", "w", encoding="utf-8") as f:
            for i in range(1, len(mmld) + 1):
                mathml = str(mmld[i]) # Get string representation of MathML
                self.dataset.append(mathml)
                f.write(repr(mathml) + "\n") # Write repr to file to preserve formatting
            print(f"Equations saved to {filename}.txt")
    
    def scrape(self): # Main function to scrape equations from a DOI
        self.make_request()
        self.make_soup()
        self.find_equations()

def main():
    doi = "10.1016/j.jmatprotec.2017.04.005"
    api_key = os.getenv("ELSEVIER_API_KEY")
    scraper = Scraper(doi, api_key)
    scraper.scrape()
    scraper.create_mathml_txt("HTC_equations") # Saves to working directory, not this file's directory

if __name__ == "__main__":
    main()