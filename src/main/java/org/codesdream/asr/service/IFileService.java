package org.codesdream.asr.service;

import org.codesdream.asr.component.datamanager.StringFile;
import org.codesdream.asr.model.file.File;

import java.io.InputStream;

public interface IFileService {

    /**
     * 储存文件
     * @param name 文件名
     * @param type 文件类型
     * @param stream 输入流
     * @return 文件ID号
     */
    Integer saveFile(String name, String type, InputStream stream);

    /**
     * 储存文件
     * @param stringFile 字符串文件对象
     * @return 文件ID号
     */
    Integer saveFile(StringFile stringFile);

    /**
     * 获得文件的输入流
     * @param fileId 文件ID号
     * @return 输入流
     */
    InputStream getFile(Integer fileId);

    /**
     * 获得字符串文件对象
     * @param fileId 文件ID号
     * @return 字符串文件对象
     */
    StringFile getStringFile(Integer fileId);

    /**
     * 删除文件(不推荐使用)
     * @param fileId 文件ID号
     * @return 布尔值
     */
    boolean deleteFile(Integer fileId);

    /**
     * 获取文件信息对象
     * @param fileId 文件ID号
     * @return 文件信息对象
     */
    File getFileInfo(Integer fileId);

}
