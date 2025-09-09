package net.javaguides.sms.controller;

import net.javaguides.sms.entity.FileMetadata;
import net.javaguides.sms.service.FileStorageService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/files")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String listFiles(Model model) {
        List<FileMetadata> files = fileStorageService.listAll();
        model.addAttribute("files", files);
        return "files";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                           Authentication auth,
                           RedirectAttributes ra) {
        try {
            fileStorageService.store(file, auth.getName());
            ra.addFlashAttribute("message", "File uploaded successfully");
        } catch (IOException e) {
            ra.addFlashAttribute("error", "Failed to upload file: " + e.getMessage());
        }
        return "redirect:/files";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable Long id) throws IOException {
        FileMetadata metadata = fileStorageService.listAll().stream()
                .filter(f -> f.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("File not found"));

        byte[] data = fileStorageService.load(metadata.getStoredName());
        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getOriginalName() + "\"")
                .contentType(MediaType.parseMediaType(metadata.getContentType()))
                .contentLength(data.length)
                .body(resource);
    }

    @PostMapping("/{id}/delete")
    public String deleteFile(@PathVariable Long id, RedirectAttributes ra) {
        try {
            fileStorageService.delete(id);
            ra.addFlashAttribute("message", "File deleted successfully");
        } catch (IOException e) {
            ra.addFlashAttribute("error", "Failed to delete file: " + e.getMessage());
        }
        return "redirect:/files";
    }
}
