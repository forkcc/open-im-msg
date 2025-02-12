package forkcc.openim.broker.cmd;

import cn.hutool.json.JSONObject;
import io.netty.channel.ChannelHandlerContext;

public abstract class AbstractCMD {
    /**
     * 命令器名字
     * @return
     */
    public abstract String getName();

    /**
     * 执行命令
     */
    public abstract JSONObject execute(ChannelHandlerContext context, JSONObject object);
}
