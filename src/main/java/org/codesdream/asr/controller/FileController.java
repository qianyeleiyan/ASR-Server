package org.codesdream.asr.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.codesdream.asr.component.json.model.JsonableFile;
import org.codesdream.asr.configure.AppConfigure;
import org.codesdream.asr.exception.badrequest.IllegalException;
import org.codesdream.asr.exception.innerservererror.RuntimeIOException;
import org.codesdream.asr.service.IFileService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Slf4j
@RestController
@Api("文件服务类接口")
@RequestMapping("file")
public class FileController {

    @Resource
    private IFileService fileService;

    @Resource
    private AppConfigure configure;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("文件上传接口")
    public JsonableFile uploadFile(@RequestParam("file") MultipartFile file){
        String filename = file.getOriginalFilename();

        String[] strArray = filename.split("\\.");
        int suffixIndex = strArray.length -1;
        String fileType = strArray[suffixIndex];

        log.info(String.format("File Upload filename %s", filename));
        log.info(String.format("File Upload fileType %s", fileType));

        // 检查文件大小
        if(file.getSize() > configure.getFileMaxSize()) throw new IllegalException(Long.toString(file.getSize()));

        if(fileType.equals("doc") || fileType.equals("docx")){
            try {
                byte[] fileData = file.getBytes();
                ByteArrayInputStream stream = new ByteArrayInputStream(fileData);
                Integer fileId =  fileService.saveFile(filename, fileType, stream);

                // 填写返回JSON
                JsonableFile jsonableFile = new JsonableFile();
                jsonableFile.setFileId(fileId);
                jsonableFile.setFilename(filename);
                jsonableFile.setType(fileType);
                return jsonableFile;

            } catch (IOException e){
                throw new RuntimeIOException(filename);
            }

        }
        else throw new IllegalException(fileType);
    }
}
