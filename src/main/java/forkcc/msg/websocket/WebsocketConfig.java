package forkcc.msg.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebsocketConfig {
    @Value("${server.port:8443}")
    private int serverPort;
    @Bean(destroyMethod = "shutdownGracefully")
    public EventLoopGroup bossGroup(){
        return new NioEventLoopGroup();
    }
    @Bean(destroyMethod = "shutdownGracefully")
    public EventLoopGroup workerGroup(){
        return new NioEventLoopGroup();
    }
    @Bean
    public ServerBootstrap serverBootstrap(EventLoopGroup bossGroup, EventLoopGroup workerGroup, WebsocketChannelInitializer channelInitializer) throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)  // 设置 TCP 参数
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .handler(new LoggingHandler(LogLevel.INFO)) // 配置日志处理器
                .childHandler(channelInitializer);
        serverBootstrap.bind(serverPort).sync();
        return serverBootstrap;
    }
}
