package com.atguigu.tingshu.album.service.impl;

import com.atguigu.tingshu.album.service.FileService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * @author atguigu-mqx
 * @ClassName FileServiceImpl
 * @description: TODO
 * @date 2023年11月22日
 * @version: 1.0
 */
@Service
@RefreshScope
public class FileServiceImpl implements FileService {

    //  获取服务器地址
    @Value("${minio.endpointUrl}")
    private String endpointUrl;

    //  获取服务器地址
    @Value("${minio.accessKey}")
    private String accessKey;

    //  获取服务器地址
    @Value("${minio.secreKey}")
    private String secreKey;

    //  获取服务器地址
    @Value("${minio.bucketName}")
    private String bucketName;

    @Override
    public String fileUpload(MultipartFile file) {
        String url = "";
        try {
            // Create a minioClient with the MinIO server playground, its access key and secret key.
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint(endpointUrl)
                            .credentials(accessKey, secreKey)
                            .build();

            // Make 'asiatrip' bucket if not exist.
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                // Make a new bucket called 'asiatrip'.
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            } else {
                System.out.println("Bucket "+bucketName+" already exists.");
            }
            //  文件名称：不能重复！ 并且要获取到文件后缀名. atguigu.jpg;
            String fileName = UUID.randomUUID().toString().replaceAll("-","")+"."+ FilenameUtils.getExtension(file.getOriginalFilename());
            //  文件上传：
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(
                                    file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            //  给url 赋值：
            //  //img13.360buyimg.com/seckillcms/s140x140_jfs/t1/166497/1/37077/39129/652795bcF0845ad6f/99c44c6ec1de48a3.jpg.webp
            //  http://192.168.200.130:9000/atguigutingshu/xxx.jpg
            url = endpointUrl+"/"+bucketName+"/"+fileName;
            System.out.println("url:\t"+url);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        } catch (InsufficientDataException e) {
            throw new RuntimeException(e);
        } catch (InternalException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (XmlParserException e) {
            throw new RuntimeException(e);
        }
        //  返回数据
        return url;
    }
}
