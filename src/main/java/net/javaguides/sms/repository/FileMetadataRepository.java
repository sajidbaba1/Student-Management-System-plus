package net.javaguides.sms.repository;

import net.javaguides.sms.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    List<FileMetadata> findByUploadedBy(String uploadedBy);
}
