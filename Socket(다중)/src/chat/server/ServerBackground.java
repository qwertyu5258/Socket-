package chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ServerBackground {

	private ServerSocket serverSocket;
	private Socket socket;
	private ServerGui gui;
	private String msg;

	
	private Map<String, DataOutputStream> clientsMap = new HashMap<String, DataOutputStream>();

	public final void setGui(ServerGui gui) {
		this.gui = gui;
	}

	public void setting() throws IOException {
			Collections.synchronizedMap(clientsMap); // ?´ê±? êµí†µ? •ë¦? ?•´ì¤ë‹ˆ?‹¤^^
			serverSocket = new ServerSocket(7777);
			while (true) {
			
				System.out.println("?„œë²? ??ê¸°ì¤‘...");
				socket = serverSocket.accept(); // ë¨¼ì? ?„œë²„ê? ?• ?¼?? ê³„ì† ë°˜ë³µ?•´?„œ ?‚¬?š©?ë¥? ë°›ëŠ”?‹¤.
				System.out.println(socket.getInetAddress() + "?—?„œ ? ‘?†?–ˆ?Šµ?‹ˆ?‹¤.");
				// ?—¬ê¸°ì„œ ?ƒˆë¡œìš´ ?‚¬?š©? ?“°? ˆ?“œ ?´?˜?Š¤ ?ƒ?„±?•´?„œ ?†Œì¼“ì •ë³´ë?? ?„£?–´ì¤˜ì•¼ê² ì£ ?!
				Receiver receiver = new Receiver(socket);
				receiver.start();
			}
	}

	public static void main(String[] args) throws IOException {
		ServerBackground serverBackground = new ServerBackground();
		serverBackground.setting();
	}

	// ë§µì˜?‚´?š©(?´?¼?´?–¸?Š¸) ???¥ê³? ?‚­? œ
	public void addClient(String nick, DataOutputStream out) throws IOException {
		sendMessage(nick + "?‹˜?´ ? ‘?†?•˜?…¨?Šµ?‹ˆ?‹¤.");
		clientsMap.put(nick, out);
	}

	public void removeClient(String nick) {
		sendMessage(nick + "?‹˜?´ ?‚˜ê°??…¨?Šµ?‹ˆ?‹¤.");
		clientsMap.remove(nick);
	}

	// ë©”ì‹œì§? ?‚´?š© ? „?ŒŒ
	public void sendMessage(String msg) {
		Iterator<String> it = clientsMap.keySet().iterator();
		String key = "";
		while (it.hasNext()) {
			key = it.next();
			try {
				clientsMap.get(key).writeUTF(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// -----------------------------------------------------------------------------
	class Receiver extends Thread {
		private DataInputStream in;
		private DataOutputStream out;
		private String nick;

		/** XXX 2. ë¦¬ì‹œë²„ê? ?•œ?¼?? ?ê¸? ?˜¼??„œ ?„¤?Š¸?›Œ?¬ ì²˜ë¦¬ ê³„ì†..?“£ê¸?.. ì²˜ë¦¬?•´ì£¼ëŠ” ê²?. */
		public Receiver(Socket socket) throws IOException {
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			nick = in.readUTF();
			addClient(nick, out);
		}

		public void run() {
			try {// ê³„ì† ?“£ê¸°ë§Œ!!
				while (in != null) {
					msg = in.readUTF();
					sendMessage(msg);
					gui.appendMsg(msg);
				}
			} catch (IOException e) {
				removeClient(nick);
			}
		}
	}
}
