import threading
import time

import gui
from clipboardHelper import getSink
from connectionHandler import service, _running, stop, start
from gui import shoSignIn, shoHome
from signinHelper import getEmail


def readyApp():
	if not getEmail():
		shoSignIn()
	else:
		shoHome()
		if not _running:
			threading.Thread(target=start, daemon=True).start()


def trackService():
	global _running
	while True:
		if not getSink():
			stop()
		else:
			if not _running:
				_running = True
				threading.Thread(target=start, daemon=True).start()
		time.sleep(1)
readyApp()
threading.Thread(target=trackService, daemon=True).start()

gui.app.MainLoop()