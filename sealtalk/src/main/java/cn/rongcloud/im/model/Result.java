package cn.rongcloud.im.model;

import cn.rongcloud.im.common.ErrorCode;
import cn.rongcloud.im.common.NetConstant;
import cn.rongcloud.im.utils.ToastUtils;

/**
 * 网络请求结果基础类
 * @param <T> 请求结果的实体类
 */
public class Result<T> {
    public int code;
    public T data;
    public String message;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Result(){
    }

    public Result(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getResult() {
        return data;
    }

    public void setResult(T result) {
        this.data = result;
    }

    public boolean isSuccess(){
        return code == NetConstant.REQUEST_SUCCESS_CODE;
    }

    public String getErrorMessage(){
        return ErrorCode.fromCode(code).getMessage();
    }
}
