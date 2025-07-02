import asyncio
import threading
import time

from ably import AblyRealtime
from ably.types.message import Message
from clipboardHelper import setText, getText, getSink
from userHelper import getEmail

_running = False

async def main():
	if not getSink():
		return
	global _running
	_running = True
	API_KEY = "FpOJ7Q.mL0X1w:cOs9eYnIXR7sHXQ1sNwyApmZ3MrzERp_gJoOcW_bp3U"
	client = AblyRealtime(API_KEY)
	channel = client.channels.get(getEmail())
	def on_connection_state_change(state_change):
		print(f"🔁 Connection State: {state_change.current}")
	client.connection.on(on_connection_state_change)

	async def handle_message(message: Message):
		print(f"📥 RECEIVED: {message.name} -> {message.data} (from {message.client_id})")
		setText(message.data)

	await channel.subscribe(handle_message)
	print("✅ Subscribed to channel")

	while _running:
		if not getSink():
			break
		text = getText()
		if text:
			await channel.publish("clipboard", text)
			print(f"📤 Published: {text}")
		await asyncio.sleep(1)
	print("🛑 Exiting main loop.")
	return

def start():
	asyncio.run(main())

def stop():
	global _running
	_running = False


service = threading.Thread(target=start, daemon=True)

