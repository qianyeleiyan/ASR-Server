package org.codesdream.asr.component.datamanager;

import org.codesdream.asr.exception.innerservererror.StringFileConvertException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 字符串文件生成器
 */
@Component
public class StringFileGenerator {

    @Resource
    private SHA256Encoder encoder;

    /**
     * 用过读入流创建一个字符串文件
     * @param name 文件名
     * @param type 文件类型
     * @param stream 输入流
     * @return 字符串文件对象
     */
    public Optional<StringFile> generateStringFile(String name, String type,InputStream stream){
        StringFile file = new StringFile();
        file.setName(name);
        file.setType(type);

        // 字符串内容计算
        file.setStrData(generateFile2String(stream));
        if(file.getStrData() == null) return Optional.empty();

        // 相关校验值计算
        file.setSha256(generateSHA256Checker(file.getStrData()));
        file.setSize(file.getStrData().length());
        return Optional.of(file);
    }

    /**
     * 一次性读取输入流中的文件数据
     * @param stream 输入流
     * @return 文件数据
     */
    private byte[] readSteamAll(InputStream stream) {
        try {
            byte[] bytes = new byte[stream.available()];

            //检查文件书否完全读取
            if (stream.read(bytes) != bytes.length) return null;
            else return bytes;
        } catch (IOException e){
            return null;
        }
    }

    /**
     * 将文件数据压缩（Gzip），然后用Base64编码为字符串
     * @param stream 输入流
     * @return 文件数据编码
     */
    private String generateFile2String(InputStream stream){
        ByteArrayOutputStream zipDataStream = new ByteArrayOutputStream();
        try {

            // 解压缩
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(zipDataStream);
            byte[] bytes = readSteamAll(stream);
            if(bytes == null) return null;
            gzipOutputStream.write(bytes);
            gzipOutputStream.close();

            // 编码转换
            return Base64.getEncoder().encodeToString(zipDataStream.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 生成字符串文件的校验码
     * @param str 文件散列值检查
     * @return
     */
    private String generateSHA256Checker(String str){
        return encoder.encode(str);
    }

    /**
     * 检查文件内容是否正确，包括大小与校验码
     * @param file 字符串文件对象
     * @return 布尔值
     */
    public boolean checkStringFile(StringFile file){
        return file.getStrData().length() == file.getSize()
                && encoder.match(file.getStrData(), file.getSha256());
    }

    /**
     * 从字符串文件中读取真实的文件数据
     * @param file 字符串文件对象
     * @return 输入流
     */
    public InputStream readFileString(StringFile file){
        try {
            // 字符串转换为二进制数据
            byte[] bytes = Base64.getDecoder().decode(file.getStrData());
            GZIPInputStream stream = new GZIPInputStream(new ByteArrayInputStream(bytes), bytes.length);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // 数据解压缩
            int readBits = 0;
            byte[] rawBytes = new byte[1024];
            while ((readBits = stream.read(rawBytes)) != -1) {
                outputStream.write(rawBytes, 0, readBits);
            }

            stream.close();
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            throw new StringFileConvertException("Read FileString Failed");
        }
    }
}
