package org.codesdream.asr.model.file;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "file")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    // 文件名
    private String name;

    // 文件哈希码
    private String hash;

    // 文件类型
    private String type;

    // 文件大小
    private Integer size;

    // 储存名
    private String storageName;

    // 文件路径
    private String path = "/";

    // 文件上传时间
    private Date date = new Date();
}
