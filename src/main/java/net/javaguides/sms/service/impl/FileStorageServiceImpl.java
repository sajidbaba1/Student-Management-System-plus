package net.javaguides.sms.service.impl;

import net.javaguides.sms.entity.FileMetadata;
import net.javaguides.sms.repository.FileMetadataRepository;
import net.javaguides.sms.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.storage.path:uploads}")
    private String storagePath;

    private final FileMetadataRepository fileMetadataRepository;

    public FileStorageServiceImpl(FileMetadataRepository fileMetadataRepository) {
        this.fileMetadataRepository = fileMetadataRepository;
    }

    @Transactional
    @Override
    public FileMetadata store(MultipartFile file, String uploadedBy) throws IOException {
        Path uploadDir = Paths.get(storagePath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String storedName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadDir.resolve(storedName);
        Files.copy(file.getInputStream(), filePath);

        FileMetadata metadata = new FileMetadata(
                file.getOriginalFilename(),
                storedName,
                file.getContentType(),
                file.getSize(),
                uploadedBy
        );
        return fileMetadataRepository.save(metadata);
    }

    @Override
    public byte[] load(String storedName) throws IOException {
        Path filePath = Paths.get(storagePath).resolve(storedName);
        return Files.readAllBytes(filePath);
    }

    @Override
    public List<FileMetadata> listByUser(String username) {
        return fileMetadataRepository.findByUploadedBy(username);
    }

    @Override
    public List<FileMetadata> listAll() {
        return fileMetadataRepository.findAll();
    }

    @Transactional
    @Override
    public void delete(Long fileId) throws IOException {
        FileMetadata metadata = fileMetadataRepository.findById(fileId).orElseThrow();
        Path filePath = Paths.get(storagePath).resolve(metadata.getStoredName());
        Files.deleteIfExists(filePath);
        fileMetadataRepository.deleteById(fileId);
    }
}
