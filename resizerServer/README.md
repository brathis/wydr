# REST API for Image transformations for WYDR

## Requirements
The following are needed to run the server:
- Ubuntu 18.04
- Python 3.5+
- PIP
- OpenCV / Pillow
- Flask

## Installation
### Setup Virtual Environment
To create the environment:

```bash
python3 -m venv ./nameOfEnvironment
```

To activate the environment:

```bash
source ./nameOfEnvironment/bin/activate
```
### Clone this repository

```bash
git clone https://github.com/brathis/wydr.git
cd wydr
```

### Install Dependencies

```bash
pip install -r requirements.txt
```

### Prepare and Activate the API

Preparations:
```bash
export FLASK_APP=app.py
```

To activate the REST API for local use only:
```bash
flask run
```

To activate the REST API and expose to the entire internet: (Do not forget to whitelist the corresponding port in your firewall)
```bash
flask run --host=0.0.0.0
```
