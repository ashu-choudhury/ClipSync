import os
import json
from google_auth_oauthlib.flow import InstalledAppFlow
from google.oauth2 import id_token
from google.auth.transport.requests import Request  # ✅ this is important
from connectionHandler import service
os.environ['OAUTHLIB_RELAX_TOKEN_SCOPE'] = '1'
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
SCOPES = ['openid', 'email', 'profile']
flow = InstalledAppFlow.from_client_secrets_file(os.path.join(BASE_DIR, "client_secret.json"), SCOPES)
def signIn(checking: bool = False):
	if os.path.exists(os.path.join(BASE_DIR,"user.json")):
		return json.load(open(os.path.join(BASE_DIR, "user.json")))
	if checking:
		return None
	try:
		credentials = flow.run_local_server(port=0)
		request = Request()
		info = id_token.verify_oauth2_token(
			credentials.id_token,
			request= request,
		)
		print(info["email"])
		with open(os.path.join(BASE_DIR, "user.json"), "w") as f:
			f.write(json.dumps(info))
		service.start()
		return info
	except Exception as e:
		print(e)
		return None

def  getEmail():
	if not os.path.exists(os.path.join(BASE_DIR,"user.json")):
		return None
	data = json.load(open(os.path.join(BASE_DIR, "user.json")))
	return data["email"]
