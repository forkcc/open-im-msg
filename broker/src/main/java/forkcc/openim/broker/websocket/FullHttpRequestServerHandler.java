package forkcc.openim.broker.websocket;

import forkcc.openim.broker.callback.ClientCallback;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

@Slf4j
@Component
@ChannelHandler.Sharable
@RequiredArgsConstructor
public class FullHttpRequestServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final ClientCallback callback;
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest msg) throws Exception {
        QueryStringDecoder decoder = new QueryStringDecoder(msg.uri());
        List<String> tokens = Optional.ofNullable(decoder.parameters()).orElseGet(Map::of).get("token");
        tokens = Optional.ofNullable(tokens).orElseGet(List::of);
        callback.connect(tokens);
        FullHttpRequest request =
                new DefaultFullHttpRequest(msg.protocolVersion(), msg.method(), decoder.rawPath(), msg.content().copy(), msg.headers().copy(), msg.trailingHeaders().copy());
        channelHandlerContext.fireChannelRead(request);
    }
}
