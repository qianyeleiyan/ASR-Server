package org.codesdream.asr.component.datamanager;

import lombok.Data;

/**
 * 储存字符串标识的文件（可以将文件直接转换为Json进行传输）
 */
@Data
public class StringFile {
    // 文件内容（Base64编码，Gzip算法压缩）
    private String strData = null;

    // 散列值
    private String sha256 = null;

    // 文件大小
    private Integer size = null;

    // 文件类型
    private String type  = "none";

    // 文件名
    private String name = null;

}
