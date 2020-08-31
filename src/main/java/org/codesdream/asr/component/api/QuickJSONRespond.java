package org.codesdream.asr.component.api;

import org.codesdream.asr.component.datamanager.JSONParameter;
import org.codesdream.asr.component.json.respond.JSONBaseRespondObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * JSON返回快速构造组件类（主要用于异常处理机制）
 */
@Component
public class QuickJSONRespond {

    @Resource
    private JSONParameter jsonParameter;

    /**
     * 根据对象构造获得标准的JSON响应字符串
     * @param status 状态码
     * @param msg 信息
     * @param info 附加信息
     * @param dataObject 附带对象
     * @return JSON字符串
     */
    public String getJSONStandardRespond(Integer status, String msg, String info, Object dataObject){
        JSONBaseRespondObject respondObject = new JSONBaseRespondObject(status, msg);
        if(info != null) respondObject.setInfo(info);
        else  respondObject.setInfo(null);

        respondObject.setData(dataObject);
        return jsonParameter.getJSONString(respondObject);
    }

    /**
     * 根据对象构造获得标准的JSON响应字符串
     * @param status 状态码
     * @param dataObject 附带对象
     * @return JSON字符串
     */
    public String getJSONStandardRespond(HttpStatus status, Object dataObject){
        JSONBaseRespondObject respondObject = new JSONBaseRespondObject(status.value(), status.getReasonPhrase());

        respondObject.setData(dataObject);
        return jsonParameter.getJSONString(respondObject);
    }


    /**
     * 根据对象构造获得标准的JSON响应字符串
     * @param status 状态码
     * @param info 附加信息
     * @param dataObject 附带对象
     * @return JSON字符串
     */
    public String getJSONStandardRespond(HttpStatus status, String info, Object dataObject){
        JSONBaseRespondObject respondObject = new JSONBaseRespondObject(status.value(), status.getReasonPhrase());
        if(info != null) respondObject.setInfo(info);
        else  respondObject.setInfo(null);

        respondObject.setData(dataObject);
        return jsonParameter.getJSONString(respondObject);
    }


    /**
     * 根据对象构造获得标准的JSON响应字符串
     * @param status 状态码
     * @param info 附加信息
     * @return JSON字符串
     */
    public String getJSONStandardRespond(HttpStatus status, String info){
        JSONBaseRespondObject respondObject = new JSONBaseRespondObject(status.value(), status.getReasonPhrase());
        if(info != null) respondObject.setInfo(info);
        else  respondObject.setInfo(null);

        return jsonParameter.getJSONString(respondObject);
    }

    /**
     * 获得标准的JSON响应字符串返回特定状态码的和解释息
     * @param code 状态码
     * @param msg 信息
     * @param info 附加信息
     * @return JSON字符串
     */
    public String getJSONStandardRespond(Integer code, String msg, String info){
        JSONBaseRespondObject respondObject = new JSONBaseRespondObject(code, msg);
        if(info != null) respondObject.setInfo(info);
        else  respondObject.setInfo(null);
        respondObject.setData(null);
        return jsonParameter.getJSONString(respondObject);
    }

    /**
     * 获得标准的JSON响应字符串返回(404状态)
     * @param info 附加信息
     * @return JSON字符串
     */
    public String getRespond404(String info){
        return getJSONStandardRespond(HttpStatus.NOT_FOUND, info);
    }

    /**
     * 获得标准的JSON响应字符串返回(404状态)
     * @param info 附加信息
     * @param object 附带对象
     * @return JSON字符串
     */
    public String getRespond404(String info, Object object){
        return getJSONStandardRespond(HttpStatus.NOT_FOUND, info, object);
    }

    /**
     * 获得标准的JSON响应字符串返回(500状态)
      * @param info 附加信息
     * @return JSON字符串
     */
    public String getRespond500(String info){
        return getJSONStandardRespond(HttpStatus.INTERNAL_SERVER_ERROR, info);
    }

    /**
     * 获得标准的JSON响应字符串返回(200状态)
     * @param info 附加信息
     * @return JSON字符串
     */
    public String getRespond200(String info){
        return getJSONStandardRespond(HttpStatus.OK, info);
    }

    /**
     * 获得标准的JSON响应字符串返回(200状态)
     * @param info 附加信息
     * @param object 附带对象
     * @return JSON字符串
     */
    public String getRespond200(String info, Object object){
        return getJSONStandardRespond(HttpStatus.OK, info, object);
    }

    /**
     * 获得标准的JSON响应字符串返回(403状态)
     * @param info 附加信息
     * @return JSON字符串
     */
    public String getRespond403(String info){
        return getJSONStandardRespond(HttpStatus.FORBIDDEN, info);
    }

    /**
     * 获得标准的JSON响应字符串返回(406状态)
     * @param info 附加信息
     * @return JSON字符串
     */
    public String getRespond406(String info){
        return getJSONStandardRespond(HttpStatus.NOT_ACCEPTABLE, info);
    }

    /**
     * 获得标准的JSON响应字符串返回(406状态)
     * @param info 附加信息
     * @param object 附带对象
     * @return JSON字符串
     */
    public String getRespond406(String info, Object object){
        return getJSONStandardRespond(HttpStatus.NOT_ACCEPTABLE, info, object);
    }

    /**
     * 获得标准的JSON响应字符串返回(501状态)
     * @param info 附加信息
     * @return JSON字符串
     */
    public String getRespond501(String info){
        return getJSONStandardRespond(501, "Not Implemented", info)  ;
    }

    /**
     * 获得标准的JSON响应字符串返回(401状态)
     * @param info 附加信息
     * @return JSON字符串
     */
    public String getRespond401(String info){
        return getJSONStandardRespond(401, "Unauthorized", info);
    }

    /**
     * 获得标准的JSON响应字符串返回(400状态)
     * @param info 附加信息
     * @return JSON字符串
     */
    public String getRespond400(String info){
        return getJSONStandardRespond(400, "Bad Request", info);
    }

    /**
     * 获得标准的JSON响应字符串返回(404状态)
     * @param info 附加信息
     * @param object 附带对象
     * @return JSON字符串
     */
    public String getRespond400(String info, Object object){
        return getJSONStandardRespond(400, "Bad Request", info, object);
    }

    /**
     * 获得标准的JSON响应字符串返回(400状态)
     * @param info 附加信息
     * @return JSON字符串
     */
    public String getRespond409(String info){
        return getJSONStandardRespond(409, "Conflict", info);
    }


}
