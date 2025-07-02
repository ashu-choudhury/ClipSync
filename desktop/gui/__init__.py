import wx

from .homeScreen import HomeSyncFrame
from .signinScreen import SignInFrame

class App(wx.App):
	def OnInit(self):
		print("✅ wx.App initialized")
		self.screen = None
		return True

def shoSignIn():
	app.screen = SignInFrame()
	app.screen.Show()

def shoHome():
	app.screen = HomeSyncFrame()
	app.screen.Show()


app = App()
