package lab10;

import static spark.Spark.*;

import javax.servlet.http.Cookie;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONObject;


@WebSocket
public class ChatWebSocketHandler {
	private Chat chat;
    private String sender, msg;

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        if(chat == null) chat = new Chat();
    	chat.incnextUserNumber();
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = chat.getUserName(user);
        chat.removeUser(user);
        chat.broadcastMessage(sender = "Server", msg = (username + " left the chat"));
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
    	System.out.println(message);
    	JSONObject obj = new JSONObject(message);
    	if(obj.has("username"))
    	{
    		String userName = obj.getString("username");
    		chat.InsertUser(user, userName);
    		chat.broadcastMessage(sender = "Server", msg = (userName + " joined the chat"));
    	}
    	else if(obj.has("ch"))
    	{
    		Channel chrem = chat.getChannelWithSession(user);
    		if(chrem != null) chrem.removeUser(user);
    		String chname = obj.getString("ch");
    		chat.incChannelNumber();
    		Channel ch = new Channel(chname,chat.getChannelNumber(),chat);
    		chat.insertChannel(chname, ch);
    		chat.broadcastMessage(sender = "Server", msg = chat.getUserName(user) + " created the channel " + obj.getString("ch"));
    		ch.addUser(user, chat.getUserName(user));
    		ch.broadcastMessage(sender = "Server", msg = (chat.getUserName(user) + " joined the channel " + ch.getName()));
    	}
    	else if(obj.has("channel") && !obj.getString("channel").equals("channellist") && !obj.getString("channel").equals("btn"))
    	{
    		if(chat.getChannelWithSession(user) == null)
    		{
    			Channel chadd = chat.getChannelWithName(obj.getString("channel"));
    			if(chadd!=null)
    			{
    			String username = chat.getUserName(user);
    			chadd.addUser(user, username);
    			chadd.broadcastMessage(sender = "Server", msg = (username + " joined the channel " + chadd.getName()));
    			}
    		}
    		else if(!chat.getChannelWithSession(user).equals(chat.getChannelWithName(obj.getString("channel"))))
    		{
    			Channel chrem = chat.getChannelWithSession(user);
    	    	if(chrem!=null && user != null)
    	    	{
    	    		chrem.removeUser(user);
    	    		chrem.broadcastMessage(sender = "Server", msg = (chat.getUserName(user) + " left the channel"));
    	    	}
    	    	Channel chadd = chat.getChannelWithName(obj.getString("channel"));
    			if(chadd!=null)
    			{
    			String username = chat.getUserName(user);
    			chadd.addUser(user, username);
    			chadd.broadcastMessage(sender = "Server", msg = (username + " joined the channel " + chadd.getName()));
    			}
    		}
    	}
    	else if(obj.has("msg"))
    	{
    		Channel ch = chat.getChannelWithSession(user);
    		if(ch != null)
    		{
    			ch.broadcastMessage(chat.getUserName(user), obj.getString("msg"));
    		}
    		if(ch.getIndex() == -1)
    		{
    			ch.broadcastMessage(sender = "Bot", msg = chat.getBotMessage(obj.getString("msg")));
    		}
    	}
    	else if(obj.has("leave"))
    	{
    		Channel chrem = chat.getChannelWithSession(user);
	    	if(chrem!=null && user != null)
	    	{
	    		chrem.removeUser(user);
	    		chrem.broadcastMessage(sender = "Server", msg = (chat.getUserName(user) + " left the channel"));
	    	}
    	}
    }

}