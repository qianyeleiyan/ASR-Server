package org.codesdream.asr.service;

import org.codesdream.asr.component.datamanager.FileParser;
import org.codesdream.asr.component.datamanager.StringFile;
import org.codesdream.asr.component.datamanager.StringFileGenerator;
import org.codesdream.asr.configure.AppConfigure;
import org.codesdream.asr.exception.innerservererror.RuntimeIOException;
import org.codesdream.asr.exception.innerservererror.StringFileConvertException;
import org.codesdream.asr.exception.notfound.NotFoundException;
import org.codesdream.asr.model.file.File;
import org.codesdream.asr.repository.file.FileRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * 文件服务实例
 */
@Service
public class FileService implements IFileService {

    @Resource
    private AppConfigure configure;


    @Resource
    private StringFileGenerator stringFileGenerator;

    @Resource
    private FileRepository fileRepository;

    @Resource
    private FileParser fileParser;

    /**
     * 储存文件
     * @param name 文件名
     * @param type 文件类型
     * @param stream 输入流
     * @return 文件ID号
     */
    @Override
    public Integer saveFile(String name, String type, InputStream stream) {
        Path filePath = Paths.get(configure.getFilePath());
        if(!Files.exists(filePath)){
            try {
                Files.createDirectory(filePath);
            } catch (IOException e){
                throw new RuntimeIOException(e.getMessage());
            }
        }

        File file = new File();
        file.setName(name);
        file.setPath(configure.getFilePath());
        file.setType(type);

        Optional<byte[]> bytes = fileParser.read(stream);
        if(!bytes.isPresent()) return null;

        String hash = fileParser.encode(bytes.get());

        // 运用缓存机制
        Optional<File> existFile = fileParser.find(hash);
        if (existFile.isPresent()){
            file.setStorageName(existFile.get().getStorageName());

            file.setSize(existFile.get().getSize());
        }
        else{
            file = fileParser.writeOut(file, bytes.get());
        }

        file.setHash(hash);

        return fileRepository.save(file).getId();

    }

    /**
     * 储存文件
     * @param stringFile 字符串文件对象
     * @return 文件ID号
     */
    @Override
    public Integer saveFile(StringFile stringFile) {
        saveFile(stringFile.getName(),
                stringFile.getType(),
                stringFileGenerator.readFileString(stringFile));
        return null;
    }

    /**
     * 获得文件的输入流
     * @param fileId 文件ID号
     * @return 输入流
     */
    @Override
    public InputStream getFile(Integer fileId) {
        Optional<File> optionalFile = fileRepository.findById(fileId);
        if(!optionalFile.isPresent())
            throw new NotFoundException(fileId.toString());

        File file = optionalFile.get();
        Path path = Paths.get(file.getPath(), file.getStorageName());
        try {
            return Files.newInputStream(path);
        } catch (IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 获得字符串文件对象
     * @param fileId 文件ID号
     * @return 字符串文件对象
     */
    @Override
    public StringFile getStringFile(Integer fileId) {
        Optional<File> optionalFile = fileRepository.findById(fileId);
        if(!optionalFile.isPresent())
            throw new NotFoundException(fileId.toString());
        File file = optionalFile.get();

        Optional<StringFile> stringFile =
                stringFileGenerator.generateStringFile(
                        file.getName(),
                        file.getType(),
                        getFile(fileId)
                );
        if(!stringFile.isPresent()) throw new StringFileConvertException(file.getName());
        return stringFile.get();
    }

    /**
     * 删除文件(不推荐使用)
     * @param fileId 文件ID号
     * @return 布尔值
     */
    @Override
    public boolean deleteFile(Integer fileId) {
        Optional<File> optionalFile = fileRepository.findById(fileId);
        if(!optionalFile.isPresent())
            throw new NotFoundException(fileId.toString());
        File file = optionalFile.get();

        Path path = Paths.get(file.getPath(), file.getStorageName());
        try {
            Files.delete(path);
            fileRepository.delete(file);
            return true;
        } catch (IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 获取文件信息对象
     * @param fileId 文件ID号
     * @return 文件信息对象
     */
    @Override
    public File getFileInfo(Integer fileId) {
        Optional<File> optionalFile = fileRepository.findById(fileId);
        if(!optionalFile.isPresent())
            throw new NotFoundException(fileId.toString());
        
        return optionalFile.get();
    }
}
