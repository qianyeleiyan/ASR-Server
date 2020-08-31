package org.codesdream.asr.component.json.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel("文件信息结构")
@NoArgsConstructor
public class JsonableFile {

    // 文件ID号
    private Integer fileId;

    // 文件名
    private String filename;

    // 文件类型
    private String type;
}
