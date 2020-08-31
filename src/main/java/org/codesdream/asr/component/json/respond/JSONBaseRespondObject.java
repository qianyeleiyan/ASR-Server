package org.codesdream.asr.component.json.respond;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.codesdream.asr.component.json.JSONBaseObject;

/**
 *  服务端返回的JSON对象标准模板
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JSONBaseRespondObject extends JSONBaseObject {

    // 存放返回内容
    private Object data = new EmptyDataObjectRespond();

    // 存放响应信息提示
    private String msg = "";

    // 额外信息
    private String info = null;

    // 状态
    private Integer status = 200;

    public JSONBaseRespondObject(String msg){
        super();
        this.status = 200;
        this.msg = msg;
    }

    public JSONBaseRespondObject(Integer status, String msg){
        super();
        this.status = status;
        this.msg = msg;
    }

    public void setStatusNotFound(){
        this.status = 404;
    }

    public void setStatusBadRequest(){
        this.status = 400;
    }

    public void setStatusUnauthorized(){
        this.status = 401;
    }

    public void setStatusForbidden(){
        this.status = 403;
    }

    public void setStatusInternalServerError(){
        this.status = 500;
    }

    public void setStatusOK(){
        this.status = 200;
    }
}
