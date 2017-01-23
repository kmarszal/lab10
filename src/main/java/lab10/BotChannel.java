package lab10;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

public class BotChannel extends Channel{
	
	public BotChannel(int ind,Chat chat)
	{
		super("Bot",ind,chat);
	}
	
	@Override
	public void broadcastMessage(String sender, String message) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
            	List<String> users = new LinkedList<String>();
            	users.addAll(userUsernameMap.values());
            	users.add("Bot");
            	
            	session.getRemote().sendString(String.valueOf(new JSONObject()
               		.put("userMessage", createHtmlMessageFromSender(sender, message))
                   	.put("userlist", users)
                   	.put("chlist", chat.getChannelKeySet())
               		));
            	} catch (Exception e) {
                e.printStackTrace();
            	}
        });
    }
}
