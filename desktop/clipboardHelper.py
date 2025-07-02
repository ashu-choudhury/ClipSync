import pyperclip
import os
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

lastCopiedText = ""
def setText(text: str):
	global lastCopiedText
	if text == lastCopiedText:
		return
	pyperclip.copy(text)
	lastCopiedText = text

def getText():
	global lastCopiedText
	text = pyperclip.paste()
	if text == lastCopiedText:
		return None
	else:
		return text

def setSink(isSink):
	with open(os.path.join(BASE_DIR, "isRunning.txt"), "w") as f:
			f.write(str(isSink))


def getSink() -> bool:
	if not os.path.exists(os.path.join(BASE_DIR, "isRunning.txt")):
		return False
	with open(os.path.join(BASE_DIR, "isRunning.txt"), "r") as f:
		return f.read() == "True"
