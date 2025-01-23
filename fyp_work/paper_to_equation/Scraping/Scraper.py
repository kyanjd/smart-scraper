import requests
import bs4
import os

class Scraper:
    def __init__(self, doi, api_key):
        """
        Initialise the Scraper with a DOI and API key.

        Args:
            doi (str): The DOI of the article to scrape.
            api_key (str): The API key for accessing the Elsevier API.
        """
        self.doi = doi
        self.api_key = api_key
        self.dataset = []

    def make_request(self):
        """
        Make a request to the Elsevier API to retrieve the article content.

        Raises:
            Exception: If the request fails with a status code other than 200.
        """
        response = requests.get(f"https://api.elsevier.com/content/article/doi/{self.doi}?APIKey={self.api_key}") # Make request to Elsevier API
        if response.status_code == 200:
            print("Request successful \n")
            self.full_text = response.text 
        else:
            raise Exception(f"Request failed with status code: {response.status_code} \n")

    def make_soup(self):
        """
        Parse the response text and create a BeautifulSoup object for easy parsing.
        """
        self.soup = bs4.BeautifulSoup(self.full_text, "lxml") 

    def find_equations(self):
        """
        Find all equations in the article and store them in a dictionary.
        """
        self.equation = self.soup.find_all("ce:formula")

        self.mathml_dict = {}
        for eq in self.equation:
            label = eq.find("ce:label") # Find equation number in the format (n)
            index = int(label.text[1:-1]) # Remove parentheses and convert to int
            mathml = eq.find("mml:math") 
            mathml = mathml.contents[1] # Remove \n from beginning and end along with outer tags
            self.mathml_dict[index] = mathml 
    
    def find_constants(self):
        tables = self.soup.find_all("table")
        print(self.mathml_dict)
        print(tables[0])
        # WIP
    
    def create_mathml_txt(self, file):
        """
        Create a text file containing all the MathML equations.

        Args:
            file (str): The name of the file to save the equations to.

        Raises:
            Exception: If the file name does not end with .txt.
        """
        mmld = self.mathml_dict

        if not file.endswith(".txt"):
            raise Exception("File must be a .txt file")
        
        with open(f"{file}", "w", encoding="utf-8") as f:
            for i in range(1, len(mmld) + 1):
                mathml = str(mmld[i]) # Get string representation of MathML
                self.dataset.append(mathml)
                f.write(repr(mathml) + "\n") # Write repr to file to preserve formatting
            print(f"Equations saved to {file}")
    
    def scrape(self):
        """
        Main function to scrape equations from a DOI.
        """
        self.make_request()
        self.make_soup()
        self.find_equations()
        self.find_constants()
        self.create_mathml_txt(f"Data/{self.doi}_mathml.txt")

def main():
    doi = "10.1016/j.jmatprotec.2017.04.005"
    api_key = os.getenv("ELSEVIER_API_KEY")
    scraper = Scraper(doi, api_key)
    scraper.scrape()
    scraper.create_mathml_txt("HTC_equations.txt") # Saves to working directory, not this file's directory

def test():
    doi = "10.1016/j.jmatprotec.2017.04.005"
    api_key = os.getenv("ELSEVIER_API_KEY")
    scraper = Scraper(doi, api_key)
    scraper.make_request()
    scraper.make_soup()
    scraper.find_equations()
    scraper.find_constants()

if __name__ == "__main__":
    test()