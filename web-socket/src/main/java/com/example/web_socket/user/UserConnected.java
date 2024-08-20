package com.example.web_socket.user;

import lombok.*;
import org.springframework.web.socket.WebSocketSession;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserConnected {
    private String username;
    private WebSocketSession session;
}
