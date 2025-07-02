import wx
import wx.adv

from signinHelper import signIn

class SignInFrame(wx.Frame):
	def __init__(self):
		super().__init__(None, title="ClipSync - Sign In", size=wx.Size(400, 300), style=wx.DEFAULT_FRAME_STYLE & ~(wx.RESIZE_BORDER | wx.MAXIMIZE_BOX))
		self.panel = wx.Panel(self)
		self.SetBackgroundColour(wx.Colour(255, 255, 255))
		self.Center()

		title = wx.StaticText(self.panel, label="🔗 ClipSync")
		title_font = wx.Font(18, wx.FONTFAMILY_SWISS, wx.FONTSTYLE_NORMAL, wx.FONTWEIGHT_BOLD)
		title.SetFont(title_font)
		title.Wrap(-1)

		self.status_label = wx.StaticText(self.panel, label="Please sign in with Google to continue")
		self.status_label.Wrap(-1)

		google_btn = wx.Button(self.panel, label=" Sign in with Google", size=wx.Size(240, 40))
		google_btn.SetFont(wx.Font(10, wx.FONTFAMILY_SWISS, wx.FONTSTYLE_NORMAL, wx.FONTWEIGHT_NORMAL))
		google_btn.SetForegroundColour(wx.Colour(66, 133, 244))
		google_btn.SetBackgroundColour(wx.Colour(255, 255, 255))


		google_btn.Bind(wx.EVT_BUTTON, self.on_google_sign_in)

		sizer = wx.BoxSizer(wx.VERTICAL)
		sizer.AddStretchSpacer(1)
		sizer.Add(title, 0, wx.ALIGN_CENTER | wx.BOTTOM, 8)
		sizer.Add(self.status_label, 0, wx.ALIGN_CENTER | wx.BOTTOM, 15)
		sizer.Add(google_btn, 0, wx.ALIGN_CENTER)
		sizer.AddStretchSpacer(2)

		self.panel.SetSizer(sizer)

	def on_google_sign_in(self, event):
		self.status_label.SetLabel("🔐 Signing in...")
		wx.GetApp().Yield()  # Refresh label immediately

		user = signIn()
		if user:
			email = user.get("email", "Unknown")
			self.status_label.SetLabel(f"✅ Signed in as {email}")
			from gui import shoHome
			shoHome()
			self.Destroy()
		else:
			self.status_label.SetLabel("❌ Sign in failed. Please try again.")

