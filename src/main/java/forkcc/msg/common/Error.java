package forkcc.msg.common;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Error {
    BAD_DATA(1, "只接受JSON数据"),
    JSON_MISS_CMD(1, "JSON数据缺少CMD字段");
    private int code;
    private String message;
    public String getErrorStr(){
        return JSONUtil.createObj().putOnce("code", code).putOnce("msg", message).toStringPretty();
    }
}
