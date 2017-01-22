package lab10;

import static j2html.TagCreator.article;
import static j2html.TagCreator.b;
import static j2html.TagCreator.p;
import static j2html.TagCreator.span;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

public class Channel {
	private int index;
	private String name;
	protected Map<Session, String> userUsernameMap;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((userUsernameMap == null) ? 0 : userUsernameMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Channel other = (Channel) obj;
		if (index != other.index)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (userUsernameMap == null) {
			if (other.userUsernameMap != null)
				return false;
		} else if (!userUsernameMap.equals(other.userUsernameMap))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	public int getIndex() {
		return index;
	}

	public Channel(String name, int ind) {
		this.index = ind;
		this.name = name;
		this.userUsernameMap = new ConcurrentHashMap<>();
	}

	public void addUser(Session session, String userName) {
		userUsernameMap.put(session, userName);
	}

	public boolean hasSession(Session session) {
		if (userUsernameMap.containsKey(session))
			return true;
		else
			return false;
	}

	public void removeUser(Session session) {
		userUsernameMap.remove(session);
	}

	public void broadcastMessage(String sender, String message) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
            	session.getRemote().sendString(String.valueOf(new JSONObject()
               		.put("userMessage", createHtmlMessageFromSender(sender, message))
                   	.put("userlist", userUsernameMap.values())
                   	.put("chlist", Chat.Channels.keySet())
               		));
            	} catch (Exception e) {
                e.printStackTrace();
            	}
        });
    }

	protected String createHtmlMessageFromSender(String sender, String message) {
		return article()
				.with(b(sender + " says:"), p(message),
						span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date())))
				.render();
	}
}
