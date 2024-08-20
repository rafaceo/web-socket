package com.example.web_socket.handler;

import com.example.web_socket.user.UserConnected;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WebSocketHandler extends AbstractWebSocketHandler {
    private final Map<String, UserConnected> userConnectedMap = new ConcurrentHashMap<>();



    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String response = userConnectedMap.get(session.getId()).getUsername() + " : " + message.getPayload();
        sandMessageToChat(response);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = null;
        String query = session.getUri().getQuery();
        if(query != null) {
            String[] params = query.split("&");
            for(String param : params) {
                String[] keyValue = param.split("=");
                if(keyValue[0].equals("username") && keyValue.length > 1) {
                    username = keyValue[1];
                    break;
                }
            }
        }
        if(username != null) {
            userConnectedMap.put(session.getId(), new UserConnected(username, session));
            sandMessageToChat("User : " + username + " connected");
        }else{
            session.sendMessage(new TextMessage("Error to joining"));
            session.close(new CloseStatus(666, "Error to joining"));
        }
    }

    protected void sandMessageToChat(String message){
        for(UserConnected user: userConnectedMap.values()){
            try {
                user.getSession().sendMessage(new TextMessage(message));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}
