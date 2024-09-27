package org.example.demo.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Component
public class FileUploadUtil {

    private final Path root = Paths.get("uploads");

    public String saveFile(String fileName, MultipartFile multipartFile, String code) throws IOException {
        if (!Files.exists(root)) {
            Files.createDirectories(root);
            log.info("Tạo thư muc thành công");
        }
        try {
            String nameFile = UUID.randomUUID().toString() + ".png" ;
            Files.copy(multipartFile.getInputStream(), this.root.resolve(nameFile));
            return nameFile;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean deleteFile(String filename) {
        try {
            Path filePath = this.root.resolve(filename);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return false;
    }


}
