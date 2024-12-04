package com.vinesh.SpringRest.util.AppUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.imgscalr.Scalr;
import org.springframework.web.multipart.MultipartFile;

public class AppUtil {

    // Updated method to use Paths and resolve the path correctly
    public static String getPhotoUploadPath(String filename, String folder_name, long albumId) throws IOException {
        // Define the base directory for uploads
        Path uploadDir = Paths.get("src", "main", "resources", "static", "uploads", String.valueOf(albumId),
                folder_name);

        // Create directories if they don't exist
        Files.createDirectories(uploadDir);

        // Resolve the full path by combining the directory and filename
        Path filePath = uploadDir.resolve(filename);

        // Return the full path as a string
        return filePath.toString();
    }

    public static BufferedImage getThumbnail(MultipartFile orginalFile, Integer width) throws IOException {
        BufferedImage thumbImg = null;
        BufferedImage img = ImageIO.read(orginalFile.getInputStream());
        thumbImg = Scalr.resize(img, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, width, Scalr.OP_ANTIALIAS);
        return thumbImg;
    }

    public static Resource getFileAsResource(String filename, String folderName, long album_id) throws IOException {
        Path filePath = Paths.get("src", "main", "resources", "static", "uploads", String.valueOf(album_id), folderName,
                filename);

        // Check if the file exists
        if (Files.exists(filePath)) {
            return new UrlResource(filePath.toUri());
        } else {
            return null;
        }
    }

    public static boolean delete_photo(String filename, String folder_name, long album_id) {
        try {
            Path filePath = Paths.get("src", "main", "resources", "static", "uploads", String.valueOf(album_id),
                    folder_name,
                    filename);
            File f = new File(filePath.toString());
            if (f.delete()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
