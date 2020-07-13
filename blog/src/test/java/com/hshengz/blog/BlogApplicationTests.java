package com.hshengz.blog;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BlogApplicationTests {

    @Autowired
    FastFileStorageClient storageClient; //fastdfs上传客户端


    @Test
    public void uploadIma() throws FileNotFoundException {
        File file=new File("C:\\443381924.jpeg");

        StorePath storePath = storageClient.uploadFile(new FileInputStream(file), file.length(), "jpeg", null);
        System.out.println(storePath.getFullPath());
    }

}
