import os
import json
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
def  getEmail():
	data = json.load(open(os.path.join(BASE_DIR, "user.json")))
	return data["email"]
