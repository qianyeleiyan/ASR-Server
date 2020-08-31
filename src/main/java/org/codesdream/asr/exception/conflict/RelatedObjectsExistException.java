package org.codesdream.asr.exception.conflict;

import lombok.NoArgsConstructor;

/**
 * 存在与之相关联的对象
 */
@NoArgsConstructor
public class RelatedObjectsExistException extends RuntimeException {
    public RelatedObjectsExistException(String msg){
        super(msg);
    }
}
