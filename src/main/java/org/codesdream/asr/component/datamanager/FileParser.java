package org.codesdream.asr.component.datamanager;

import org.codesdream.asr.exception.innerservererror.RuntimeIOException;
import org.codesdream.asr.model.file.File;
import org.codesdream.asr.repository.file.FileRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 文件解析器
 */
@Component
public class FileParser {

    @Resource
    private FileRepository fileRepository;

    @Resource
    private SHA512Encoder encoder;

    /**
     * 查找缓存
     * @param hash 哈希值
     * @return 文件信息对象
     */
    public Optional<File> find(String hash){
        Iterable<File> files = fileRepository.findAllByHash(hash);
        Iterator<File> fileIterator = files.iterator();
        if(!fileIterator.hasNext()) return Optional.empty();

        return Optional.of(fileIterator.next());
    }

    /**
     * 数据散列值计算
     * @param bytesData 文件数据
     * @return 散列值
     */
    public String encode(byte[] bytesData){
        return encoder.encode(Base64.getEncoder().encodeToString(bytesData));
    }

    /**
     * 读取文件数据
     * @param stream 输入流
     * @return 文件数据字节数组
     */
    public Optional<byte[]> read(InputStream stream){
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try {
            // 双重数据写出
            int readBits = 0;
            byte[] rawBytes = new byte[1024];
            while ((readBits = stream.read(rawBytes)) != -1) {
                arrayOutputStream.write(rawBytes, 0, readBits);
            }

            return Optional.of(arrayOutputStream.toByteArray());
        } catch (IOException e){
            return Optional.empty();
        }
    }

    /**
     * 将文件数据写入到文件系统
     * @param file 文件信息对象
     * @param bytesData 文件数据
     * @return 文件信息对象（更新后）
     */
    public File writeOut(File file, byte[] bytesData){
        String storageName = UUID.randomUUID().toString();

        Path path = Paths.get(file.getPath(), storageName);
        try {
            Files.createFile(path);
            OutputStream outputStream = Files.newOutputStream(path);
            ByteArrayInputStream stream = new ByteArrayInputStream(bytesData);

            // 数据写出文件系统
            int readBits = 0;
            byte[] rawBytes = new byte[1024];
            while ((readBits = stream.read(rawBytes)) != -1) {
                outputStream.write(rawBytes, 0, readBits);
            }
            outputStream.close();
            stream.close();

            file.setStorageName(storageName);
            file.setSize(bytesData.length);
            return file;
        } catch (IOException e){
            throw new RuntimeIOException(e.getMessage());
        }
    }
}
