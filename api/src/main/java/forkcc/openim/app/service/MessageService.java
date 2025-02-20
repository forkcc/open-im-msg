package forkcc.openim.app.service;

import forkcc.openim.common.dto.message.Notify;
import forkcc.openim.common.dto.message.Transparent;

import java.util.List;

/**
 * 消息发送
 */
public interface MessageService {
    /**
     * 发送透传消息
     *    消息不一定送到，主要是一些不重要的通知
     * @param toUser 接收用户
     * @param transparent 透传消息类型
     * @param extra 附加信息
     */
    void pushTransparentMessage(String toUser, Transparent transparent, String extra);

    /**
     * 发送通知
     * @param toUsers 接收用户列表
     * @param notify 通知类型
     * @param message 实体消息
     * @param extra 附加参数
     * @param persistence 是否存储
     */
    void pushNotify(List<String> toUsers, Notify notify, String message, String extra, boolean persistence);
}
