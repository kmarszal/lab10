package lab10;

import static spark.Spark.*;

import javax.servlet.http.Cookie;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONObject;


@WebSocket
public class ChatWebSocketHandler {

    private String sender, msg;

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        ++Chat.nextUserNumber;
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = Chat.userUsernameMap.get(user);
        Chat.userUsernameMap.remove(user);
        Chat.broadcastMessage(sender = "Server", msg = (username + " left the chat"));
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
    	System.out.println(message);
    	JSONObject obj = new JSONObject(message);
    	if(obj.has("username"))
    	{
    		String userName = obj.getString("username");
    		Chat.userUsernameMap.put(user, userName);
    		Chat.broadcastMessage(sender = "Server", msg = (userName + " joined the chat"));
    	}
    	else if(obj.has("ch"))
    	{
    		String chname = obj.getString("ch");
    		Channel ch = new Channel(chname,++Chat.channelNumber);
    		Chat.Channels.put(chname, ch);
    		ch.addUser(user, Chat.userUsernameMap.get(user));
    		ch.broadcastMessage(sender = "Server", msg = (Chat.userUsernameMap.get(user) + " joined the channel " + ch.getName()));
    	}
    	else if(obj.has("channel") && !obj.getString("channel").equals("channellist") && !obj.getString("channel").equals("btn"))
    	{
    		if(Chat.getChannelWithSession(user) == null)
    		{
    			Channel chadd = Chat.Channels.get(obj.get("channel"));
    			if(chadd!=null)
    			{
    			String username = Chat.userUsernameMap.get(user);
    			chadd.addUser(user, username);
    			chadd.broadcastMessage(sender = "Server", msg = (username + " joined the channel " + chadd.getName()));
    			}
    		}
    		else if(!Chat.getChannelWithSession(user).equals(Chat.Channels.get(obj.get("channel"))))
    		{
    			Channel chrem = Chat.getChannelWithSession(user);
    	    	if(chrem!=null && user != null)
    	    	{
    	    		chrem.removeUser(user);
    	    		chrem.broadcastMessage(sender = "Server", msg = (Chat.userUsernameMap.get(user) + " left the channel"));
    	    	}
    	    	Channel chadd = Chat.Channels.get(obj.get("channel"));
    			if(chadd!=null)
    			{
    			String username = Chat.userUsernameMap.get(user);
    			chadd.addUser(user, username);
    			chadd.broadcastMessage(sender = "Server", msg = (username + " joined the channel " + chadd.getName()));
    			}
    		}
    	}
    	else if(obj.has("msg"))
    	{
    		Channel ch = Chat.getChannelWithSession(user);
    		if(ch != null)
    		{
    			ch.broadcastMessage(Chat.userUsernameMap.get(user), obj.getString("msg"));
    		}
    		if(ch.getIndex() == -1)
    		{
    			ch.broadcastMessage(sender = "Bot", msg = Chat.bot.respond(obj.getString("msg")));
    		}
    	}
    	else if(obj.has("leave"))
    	{
    		Channel chrem = Chat.getChannelWithSession(user);
	    	if(chrem!=null && user != null)
	    	{
	    		chrem.removeUser(user);
	    		chrem.broadcastMessage(sender = "Server", msg = (Chat.userUsernameMap.get(user) + " left the channel"));
	    	}
    	}
    }

}