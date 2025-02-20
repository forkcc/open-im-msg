package forkcc.openim.common.dto;

import forkcc.openim.common.dto.message.MessageType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PushMessage {
    /**
     * 接收用户
     */
    private List<String> toUsers;
    /**
     * 是否持久化
     */
    private boolean persistence;
    /**
     * 消息类型
     */
    private MessageType messageType;
    /**
     * 额外参数
     */
    private String extra;
    /**
     * 文本信息
     */
    private String message;
}
