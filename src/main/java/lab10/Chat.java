package lab10;

import org.eclipse.jetty.websocket.api.*;
import org.json.*;

import java.text.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static j2html.TagCreator.*;
import static spark.Spark.*;

public class Chat {
	private HashMap<String,Channel> Channels = new HashMap<>();
    // this map is shared between sessions and threads, so it needs to be thread-safe (http://stackoverflow.com/a/2688817)
    private Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();
    private int nextUserNumber = 1; //Assign to username for next connecting user
    private int channelNumber = 0;
    private Bot bot = new Bot();

    public Chat()
    {
    	Channels = new HashMap<>();
    	userUsernameMap = new ConcurrentHashMap<>();
    	nextUserNumber = 1;
    	channelNumber = 0;
    	bot = new Bot();
    	Channels.put("Bot", new BotChannel(-1,this));
    }
    
    public void insertChannel(String chname,Channel ch)
    {
    	Channels.put(chname, ch);
    }
    
    public void incChannelNumber()
    {
    	++channelNumber;
    }
    
    public void incnextUserNumber()
    {
    	++nextUserNumber;
    }
    
    public int getChannelNumber()
    {
    	return channelNumber;
    }
    
    public void removeUser(Session user)
    {
    	userUsernameMap.remove(user);
    	Channel ch = getChannelWithSession(user);
    	ch.removeUser(user);
    }
    
    public Collection<String> getChannelKeySet()
    {
    	return Channels.keySet();
    }
    
    public void InsertUser(Session user, String UserName)
    {
    	userUsernameMap.put(user, UserName);
    }
    
    public String getUserName(Session user)
    {
    	return userUsernameMap.get(user);
    }
    
    public void setNextUserNumber(int number)
    {
    	this.nextUserNumber = number;
    }
    
    public int getNextUserNumber()
    {
    	return this.nextUserNumber;
    }
    
    public static void main(String[] args) {
    	staticFileLocation("/"); //index.html is served at localhost:4567 (default port)
        webSocket("/chat", ChatWebSocketHandler.class);
        init();
        JSONParser.downloadWeather();
    }

    //Sends a message from one user to all users, along with a list of current usernames
    public void broadcastMessage(String sender, String message) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("userMessage", createHtmlMessageFromSender(sender, message))
                    .put("userlist", userUsernameMap.values())
                    .put("chlist", Channels.keySet())
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    //Builds a HTML element with a sender-name, a message, and a timestamp,
    private String createHtmlMessageFromSender(String sender, String message) {
        return article().with(
                b(sender + " says:"),
                p(message),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
        ).render();
   }
   
   public Channel getChannelWithName(String chname)
   {
	   return Channels.get(chname);
   }
   
   public String getBotMessage(String msg)
   {
	   return bot.respond(msg);
   }
   
   public Channel getChannelWithSession(Session session)
   {
	   for(Channel ch : Channels.values())
	   {
		   if(ch.hasSession(session)) return ch;
	   }
	   return null;
   }
   
   public Channel getChannelByIndex(int index)
   {
	   for(Channel ch : Channels.values())
	   {
		   if(ch.getIndex() == index) return ch;
	   }
	   return null;
   }

}