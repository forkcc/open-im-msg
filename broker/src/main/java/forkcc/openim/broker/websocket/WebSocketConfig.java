package forkcc.openim.broker.websocket;

import forkcc.openim.broker.callback.ClientCallback;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Data
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebSocketConfig {
    private final WebsocketProperties websocketProperties;
    private final WebSocketServerHandler webSocketServerHandler;
    private final ClientCallback clientCallback;
    private final ChannelPool channelPool;
    @Bean
    public EventLoopGroup bossGroup(){
        if(Epoll.isAvailable()){
            return new EpollEventLoopGroup(1);
        }else{
            return new NioEventLoopGroup(1);
        }
    }

    @Bean
    public EventLoopGroup workerGroup(){
        if(Epoll.isAvailable()){
            return new EpollEventLoopGroup();
        }else{
            return new NioEventLoopGroup();
        }
    }
    @Bean
    public ServerBootstrap serverBootstrap(EventLoopGroup bossGroup, EventLoopGroup workerGroup){
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(Epoll.isAvailable()?EpollServerSocketChannel.class:NioServerSocketChannel.class);
        serverBootstrap.group(bossGroup, workerGroup);
        serverBootstrap.childHandler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new HttpServerCodec());
                //最大只能传2MB数据
                pipeline.addLast(new HttpObjectAggregator(1024 * 1024 * 2));
                pipeline.addLast(new FullHttpRequestServerHandler(clientCallback, websocketProperties.getContextPath(), channelPool));
                pipeline.addLast(new IdleStateHandler(10, 10, 0));
                pipeline.addLast(new WebSocketServerProtocolHandler(websocketProperties.getContextPath(), true));
                pipeline.addLast(webSocketServerHandler);
            }
        });
        serverBootstrap.bind(websocketProperties.getPort()).addListener(channelFuture->{
            if(channelFuture.isSuccess()){
                log.info("Netty started on port(s) {} (http) with context path '{}'", websocketProperties.getPort(), websocketProperties.getContextPath());
            }
        });
        return serverBootstrap;
    }
}
