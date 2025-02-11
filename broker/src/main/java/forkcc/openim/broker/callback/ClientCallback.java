package forkcc.openim.broker.callback;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientCallback {
    public boolean connect(List<String> token){
        return true;
    }
}
