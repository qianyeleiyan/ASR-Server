package org.codesdream.asr.configure;

import org.springframework.stereotype.Component;

/**
 * 应用程序常用配置信息
 * 用于常见的应用程序本身的相关信息的引用
 */
@Component
public class AppConfigure {
    /**
     * 获得应用程序的中文名
     * @return 返回包含完整内容的字符串
     */
    public String getName() {
        return "自动日程规划服务端";
    }

    /**
     * 获得应用程序的版本号
     * @return 返回版本号内容的字符串
     */
    public String getVersion() {
        return "0.0.1_200204";
    }

    /**
     * 获得应用程序的英文名
     * @return 返回包含完整内容的字符串
     */
    public String getEnglishName() {
        return "ASR";
    }

    /**
     * 获得开发小组的名称
     * @return 包含完整内容的字符串
     */
    public String getOrganization() {
        return "码梦工坊";
    }

    /**
     * 文件服务储存路径
     * @return 字符串
     */
    public String getFilePath(){
        return "./FILES/";
    }

    /**
     * 上传的文件的大小限制（字节）
     * 预设值：16MB
     * @return 数值
     */
    public Integer getFileMaxSize(){
        return 16000000;
    }
    
}
