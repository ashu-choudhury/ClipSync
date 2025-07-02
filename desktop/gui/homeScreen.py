import os.path

import wx

from clipboardHelper import getSink, setSink
from signinHelper import signIn, BASE_DIR as signinHelperBASE_DIR

class HomeSyncFrame(wx.Frame):
	def __init__(self):
		super().__init__(None, title="HomeSync", size=wx.Size(400, 220),
		style=wx.DEFAULT_FRAME_STYLE & ~(wx.RESIZE_BORDER | wx.MAXIMIZE_BOX))
		self.Bind(wx.EVT_CLOSE, self.on_close)
		self.panel = wx.Panel(self)
		self.SetBackgroundColour(wx.Colour(255, 255, 255))
		self.Center()

		# Initial user info
		self.user = signIn()

		# --- UI Elements ---
		self.sync_checkbox = wx.CheckBox(self.panel, label="Sync Clipboard")
		self.sync_checkbox.SetValue(getSink())
		self.sync_checkbox.Bind(wx.EVT_CHECKBOX, self.on_sync_toggle)


		self.login_button = wx.Button(self.panel, label="")
		self.update_login_button()

		# --- Event Bindings ---
		self.login_button.Bind(wx.EVT_BUTTON, self.on_login_or_logout)

		# --- Layout ---
		sizer = wx.BoxSizer(wx.VERTICAL)
		sizer.AddSpacer(25)
		sizer.Add(self.sync_checkbox, 0, wx.ALIGN_LEFT | wx.LEFT, 20)
		sizer.AddSpacer(25)
		sizer.Add(self.login_button, 0, wx.ALIGN_LEFT | wx.LEFT, 20)
		sizer.AddStretchSpacer()
		self.panel.SetSizer(sizer)
	def on_sync_toggle(self, event):
		setSink(self.sync_checkbox.GetValue())
	def update_login_button(self):
		if self.user:
			email = self.user.get("email", "Unknown")
			self.login_button.SetLabel(f"Logged in as {email}")
		else:
			self.login_button.SetLabel("Sign in with Google")

	def on_close(self, event):
		self.Hide()

	def on_login_or_logout(self, event):
		if self.user:
			# Logout
			self.user = None
			os.remove(os.path.join(signinHelperBASE_DIR, "user.json"))
			setSink(False)
			self.update_login_button()
			wx.MessageBox("You have been signed out.", "Logged Out", wx.OK | wx.ICON_INFORMATION)
		else:
			# Sign-in flow
			user = signIn()
			if user:
				self.user = user
				self.update_login_button()
				wx.MessageBox(f"Signed in as {user['email']}", "Login Successful", wx.OK | wx.ICON_INFORMATION)
			else:
				wx.MessageBox("Login failed or canceled.", "Error", wx.OK | wx.ICON_ERROR)

