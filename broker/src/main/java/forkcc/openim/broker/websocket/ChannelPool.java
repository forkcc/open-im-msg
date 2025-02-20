package forkcc.openim.broker.websocket;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 连接池
 *    断开的连接会自动删除
 */
@Slf4j
@Component
public class ChannelPool{
    private static final ExecutorService MSG_THREAD_POOL = Executors.newVirtualThreadPerTaskExecutor();
    /**
     * 客户端连接池
     */
    private static final DefaultChannelGroup GROUP_LIST = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 添加连接成功的客户端
     */
    public void addChannel(Channel channel){
        GROUP_LIST.add(channel);
    }

    /**
     * 发送消息到匹配的客户端
     */
    public void writeMessage(WebSocketFrame frame, ChannelMatcher matcher){
        GROUP_LIST.parallelStream().filter(matcher::matches).forEach(channel -> {
            MSG_THREAD_POOL.execute(()->{
                try{
                    channel.writeAndFlush(frame);
                }catch (Exception e){
                    log.error("消息推送失败", e);
                }
            });
        });
    }
}
