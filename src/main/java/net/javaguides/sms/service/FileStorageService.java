package net.javaguides.sms.service;

import net.javaguides.sms.entity.FileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileStorageService {
    FileMetadata store(MultipartFile file, String uploadedBy) throws IOException;
    byte[] load(String storedName) throws IOException;
    List<FileMetadata> listByUser(String username);
    List<FileMetadata> listAll();
    void delete(Long fileId) throws IOException;
}
