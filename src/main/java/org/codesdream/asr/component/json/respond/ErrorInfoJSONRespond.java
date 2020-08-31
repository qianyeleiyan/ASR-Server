package org.codesdream.asr.component.json.respond;

import lombok.Data;

import java.util.Date;

/**
 * 错误信息对象
 */
@Data
public class ErrorInfoJSONRespond {
    String exception = null;
    String exceptionMessage = null;
    Date date = null;
}
