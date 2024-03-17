package Constant;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class NfoHelperResult<T> implements Serializable {
    private Boolean success;
    private T data;
    private String info;

    public NfoHelperResult(Boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public NfoHelperResult(T data) {
        this.success = Boolean.TRUE;
        this.data = data;
    }

    public NfoHelperResult(Boolean success, String info, T data) {
        this.success = success;
        this.info = info;
        this.data = data;
    }
}
