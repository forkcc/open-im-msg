package forkcc.openim.broker.websocket;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "websocket")
public class WebsocketProperties {
    private int port;
    private String contextPath;
}
