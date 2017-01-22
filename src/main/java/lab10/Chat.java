package lab10;

import org.eclipse.jetty.websocket.api.*;
import org.json.*;

import java.text.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static j2html.TagCreator.*;
import static spark.Spark.*;

public class Chat {
	static HashMap<String,Channel> Channels = new HashMap<>();
    // this map is shared between sessions and threads, so it needs to be thread-safe (http://stackoverflow.com/a/2688817)
    static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();
    static int nextUserNumber = 1; //Assign to username for next connecting user
    static int channelNumber = 0;
    static Bot bot = new Bot();

    public static void main(String[] args) {
        staticFileLocation("/"); //index.html is served at localhost:4567 (default port)
        webSocket("/chat", ChatWebSocketHandler.class);
        init();
        Channels.put("Bot", new BotChannel(-1));
        JSONParser.downloadWeather();
    }

    //Sends a message from one user to all users, along with a list of current usernames
    public static void broadcastMessage(String sender, String message) {
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
    private static String createHtmlMessageFromSender(String sender, String message) {
        return article().with(
                b(sender + " says:"),
                p(message),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
        ).render();
   }
    
   public static Channel getChannelWithSession(Session session)
   {
	   for(Channel ch : Channels.values())
	   {
		   if(ch.hasSession(session)) return ch;
	   }
	   return null;
   }
   
   public static Channel getChannelByIndex(int index)
   {
	   for(Channel ch : Channels.values())
	   {
		   if(ch.getIndex() == index) return ch;
	   }
	   return null;
   }

}