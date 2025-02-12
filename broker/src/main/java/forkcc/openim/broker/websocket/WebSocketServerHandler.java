package forkcc.openim.broker.websocket;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import forkcc.openim.broker.callback.ClientCallback;
import forkcc.openim.broker.cmd.AbstractCMD;
import forkcc.openim.common.exception.BizException;
import forkcc.openim.common.kit.AssertKit;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final List<AbstractCMD> cmdList;
    private final ClientCallback callback;
    private static final AttributeKey<String> TR_ID = AttributeKey.newInstance("tr.id");
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame webSocketFrame) throws Exception {
        String json = webSocketFrame.text();
        if(StrUtil.isBlank(json) || !JSONUtil.isTypeJSON(json) || JSONUtil.isTypeJSONArray(json)){
            return;
        }
        JSONObject object = JSONUtil.parseObj(json);
        AssertKit.validState(StrUtil.isNotBlank(object.getStr("uuid")),new BizException("缺少uuid参数"));
        AssertKit.validState(StrUtil.isNotBlank(object.getStr("trid")),new BizException("缺少trid参数"));
        MDC.put("uuid", object.getStr("uuid"));
        MDC.put("trid", object.getStr("trid"));
        if(StrUtil.isBlank(ctx.channel().attr(TR_ID).get())){
            ctx.channel().attr(TR_ID).set(object.getStr("trid"));
        }else if(!Objects.equals(ctx.channel().attr(TR_ID).get(), object.getStr("trid"))){
            ctx.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonPrettyStr(JSONUtil.createObj().putOnce("code", -2).putOnce("message", "trid变动"))));
        }
        Optional<AbstractCMD> optional = cmdList.stream().filter(cmd -> Objects.equals(object.getStr("cmd"), cmd.getName())).findFirst();
        if(optional.isPresent()){
            JSONObject data = optional.get().execute(ctx ,object.getJSONObject("data"));
            JSONObject view = JSONUtil.createObj().putOnce("code", 0).putOnce("message", "成功").putOnce("data", data);
            ctx.writeAndFlush(new TextWebSocketFrame(view.toStringPretty()));
        }else{
            ctx.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonPrettyStr(JSONUtil.createObj().putOnce("code", -1).putOnce("message", "未知命令"))));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(cause instanceof BizException e){
            ctx.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonPrettyStr(JSONUtil.createObj().putOnce("code", 1).putOnce("message", e.getMessage()))));
            return;
        }
        log.error("服务器出现异常", cause);
        ctx.writeAndFlush(new TextWebSocketFrame(
                JSONUtil.toJsonPrettyStr(JSONUtil.createObj().putOnce("code", 1).putOnce("message", "服务器未知异常"))));
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            ctx.writeAndFlush(new PingWebSocketFrame()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }
    }
}
