package com.chinamobile.upload2obs.task;

import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.model.DeleteObjectResult;
import com.obs.services.model.ObsObject;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class ObsConnection {
    private volatile static ObsClient INSTANCE;

    private ObsConnection() {
    }

    private static ObsClient getInstance() {
        if (INSTANCE == null) {
            synchronized (ObsConnection.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ObsClient("D3440C7BC473DDDCFF03", "x3T22DFoFQ8cdaNMj8RuEOxwlNEAAAGCxHPd3Fwx", "zzobs1.zj.chinamobile.com");
                }
            }
        }
        return INSTANCE;
    }



    public static void uploadingFile(String bucketName, String ObjectName, InputStream inputStream){
        try {
            log.info("Uploading file");
            log.info("bucketName" +  bucketName + "");
            log.info("ObjectName" +  ObjectName + "");
            getInstance().putObject(bucketName, ObjectName, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ObsServerUtils.uploadingImage：上传" + "bytes[]时  " + "文件出现问题");
        }

    }

    /**
     * 下载文件
     * @param bucketName 桶名称
     * @param ObjectName 文件名称-唯一
     * @return ByteArrayOutputStream 输出字符流
     *
     * */
    public static ByteArrayOutputStream downloadFile(String bucketName, String ObjectName){
        // 查看桶是否存在，不存在则创建
//        verifyAndAddBucket(bucketName);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ObsObject object = getInstance().getObject(bucketName, ObjectName);
            InputStream input = object.getObjectContent();
            byte[] b = new byte[1024];

            int len;
            while ((len = input.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            bos.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("ObsServerUtils.downloadFile：下载文件时出现问题");
        }
        return bos;
    }

    /**
     * 删除文件
     * @param bucketname 桶名称
     * @param objectname 文件名称-唯一
     *
     * */
    public boolean deleteObject(String bucketname,String objectname){
        DeleteObjectResult deleteObjectResult = getInstance().deleteObject(bucketname, objectname);
        return deleteObjectResult.isDeleteMarker();
    }

}
