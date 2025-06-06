This repository contains the source code for the project entitled:

# Automated Implementation of Boundary Condition Research Using Large Langauge Models (LLMs)

'''text
smart-scraper/
├── .env                      # Environment variables
├── .gitattributes            # Git attributes file
├── .gitignore                # Git ignore file
├── environment.yml           # Conda environment specification
├── README.md                 # Repository documentation
├── requirements.txt          # Python dependencies
│
├── .vscode/                  # VS Code configuration
│   └── settings.json
│
├── archived_legacy_code/     # Legacy code
│   ├── ConstantsCoursework1.m
│   ├── converter.py
│   ├── friction_schuler.py
│   ├── functional_module.ipynb
│   ├── ihtc_template.py
│   ├── notes.txt
│   ├── paper_url.txt
│   ├── README.md
│   ├── requirements.txt
│   ├── scraper.py            # Original scraper implementation
│   ├── template.py
│   ├── test.py
│   ├── figures/
│   ├── scraped_python/       # Generated Python code from scraping
│   └── scraped_txt/          # Scraped text content
│
├── fyp_work/                 # Final year project work
│   └── __pycache__/
│
├── models/                   # Trained models
│   └── checkpoint-100/       # Model checkpoint
│
└── src/                      # Source code
    ├── __init__.py
    ├── fig.ipynb             # Visualization notebook
    ├── main.py               # Main application entry point
    ├── test.ipynb            # Testing notebook
    ├── TODO.md               # Project to-do list
    ├── __pycache__/
    │
    ├── Data/                 # Data files
    │   ├── gemini_test_4_predictions.txt
    │   ├── gemini_test_4.csv
    │   ├── gemini_test_5.csv
    │   ├── HTC_equations.txt
    │   ├── HTC_gemini5_predictions.txt
    │   └── p20.csv
    │
    ├── Docs/                 # Documentation
    │   ├── DOIs
    │   ├── Friction Modelling.pdf
    │   ├── HTC Paper.pdf
    │   └── Material Property Modelling.pdf
    │
    ├── Figures/              # Generated figures
    │   ├── bothIHTC.svg
    │   ├── boxplot_metrics.svg
    │   ├── dependency_graph.pdf
    │   ├── equation_type_distribution.svg
    │   ├── Friction_pie.svg
    │   └── ...
    │
    ├── Gemini/               # Google Gemini model integration
    │   └── training_script.ipynb
    │
    ├── Generation/           # Code generation
    │   └── examples.ipynb
    │
    ├── paper_to_equation/    # Paper equation extraction
    │
    ├── Postprocessing/       # Post-processing tools
    │   └── SOE_EquationGraph.py
    │
    ├── Scraping/             # New scraper implementation
    │   └── Scraper.py
    │
    └── T5/                   # T5 model integration
        └── colab_training_script.ipynb
