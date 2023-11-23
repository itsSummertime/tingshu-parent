package com.atguigu.tingshu.album.api;

import com.atguigu.tingshu.album.config.MinioConstantProperties;
import com.atguigu.tingshu.album.service.FileService;
import com.atguigu.tingshu.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;



@Tag(name = "上传管理接口")
@RestController
@RequestMapping("api/album")
public class FileUploadApiController {

    @Autowired
    private MinioConstantProperties minioConstantProperties;

    @Autowired
    private FileService fileService;

    //  http://127.0.0.1/api/album/fileUpload
    //  文件上传：使用什么技术.
    @Operation(summary = "文件上传")
    @PostMapping("fileUpload")
    public Result fileUpload(MultipartFile file){
        //  调用服务层方法. 应该返回文件上传的地址?
        String url = fileService.fileUpload(file);
        //  返回
        return Result.ok(url);
    }

    public static void main(String[] args) {
        String name = "atg.uigu.jpg";
        //  第一种就是string 常用方法截取. lastIndexOf(".") subString();
        //  第二种方案就是使用工具类获取文件后缀。
        System.out.println(FilenameUtils.getExtension(name));

    }

}