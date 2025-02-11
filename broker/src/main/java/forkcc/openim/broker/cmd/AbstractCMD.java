package forkcc.openim.broker.cmd;

import cn.hutool.json.JSONObject;

public abstract class AbstractCMD<E> {
    /**
     * 命令器名字
     * @return
     */
    public abstract String getName();

    /**
     * 执行命令
     */
    public abstract void execute(JSONObject object);
}
