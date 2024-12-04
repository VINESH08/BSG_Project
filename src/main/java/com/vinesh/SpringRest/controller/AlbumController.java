package com.vinesh.SpringRest.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.vinesh.SpringRest.model.Account;
import com.vinesh.SpringRest.model.Album;
import com.vinesh.SpringRest.model.Photo;
import com.vinesh.SpringRest.payload.auth.Album.AlbumPayloadDTO;
import com.vinesh.SpringRest.payload.auth.Album.AlbumPayloadViewDTO;
import com.vinesh.SpringRest.payload.auth.Album.PhotoDto;
import com.vinesh.SpringRest.payload.auth.Album.PhotoPayloadDto;
import com.vinesh.SpringRest.payload.auth.Album.PhotoViewDto;
import com.vinesh.SpringRest.service.AccountService;
import com.vinesh.SpringRest.service.AlbumService;
import com.vinesh.SpringRest.service.PhotoService;
import com.vinesh.SpringRest.util.AppUtils.AppUtil;
import com.vinesh.SpringRest.util.constants.AlbumError;

import java.awt.image.BufferedImage;
import java.io.File;
import org.springframework.http.HttpHeaders;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.catalina.connector.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.core.io.Resource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/album")
@Tag(name = "Album Controller", description = "Controller for albums like photo and media")
@Slf4j
public class AlbumController {
    static final String PHOTOS_FOLDER_NAME = "photos";
    static final String THUMBNAIL_FOLDER_NAME = "thumbnamils";
    static final int THUMBNAIL_WIDTH = 300;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AlbumService albumService;
    @Autowired
    private PhotoService photoService;
    // Photo photo = new Photo();

    @PostMapping(value = "/add")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add an album")
    @SecurityRequirement(name = "Vinesh-demo-api")
    public ResponseEntity<AlbumPayloadViewDTO> addalbum(@Valid @RequestBody AlbumPayloadDTO albumPayloadDTO,
            Authentication authentication) {
        try {
            Album album = new Album();
            album.setName(albumPayloadDTO.getName());
            album.setDescription(albumPayloadDTO.getDescription());
            String email = authentication.getName();
            Optional<Account> optionalaccount = accountService.findByEmail(email);
            album.setAccount(optionalaccount.get());
            album = albumService.save(album);
            AlbumPayloadViewDTO albumPayloadViewDTO = new AlbumPayloadViewDTO(album.getId(), album.getName(),
                    album.getDescription(), null);
            return ResponseEntity.ok(albumPayloadViewDTO);

        } catch (Exception e) {
            log.debug(AlbumError.ALBUM_ERROR.toString() + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping(value = "/album")
    @Operation(summary = "Get List of album")
    @SecurityRequirement(name = "Vinesh-demo-api")
    public List<AlbumPayloadViewDTO> geList(Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();
        List<AlbumPayloadViewDTO> list = new ArrayList<>();
        for (Album album : albumService.findByAccount_id(account.getId())) {

            List<PhotoDto> photoDtos = new ArrayList<>();
            for (Photo photo : photoService.findByAlbum_id(album.getId())) {
                String link = "/" + album.getId() + "/photos/" + photo.getId() + "/download-photo";
                photoDtos.add(new PhotoDto(photo.getId(), photo.getName(), photo.getDescription(), photo.getFilename(),
                        link));
            }
            list.add(new AlbumPayloadViewDTO(album.getId(), album.getName(), album.getDescription(), photoDtos));

        }
        return list;
    }

    @PostMapping(value = "/{album_id}/upload-photos", consumes = { "multipart/form-data" })
    @Operation(summary = "Upload photo into album")
    @ApiResponse(responseCode = "400", description = "Please check the payload or token")
    @SecurityRequirement(name = "Vinesh-demo-api")
    public ResponseEntity<List<HashMap<String, List<String>>>> photos(
            @RequestPart(required = true) MultipartFile[] files,
            @PathVariable long album_id, Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();
        Optional<Album> optionaAlbum = albumService.findById(album_id);
        Album album;
        if (optionaAlbum.isPresent()) {
            album = optionaAlbum.get();
            if (account.getId() != album.getAccount().getId()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        List<String> fileNamesWithSuccess = new ArrayList<>();
        List<String> fileNamesWithError = new ArrayList<>();

        Arrays.asList(files).stream().forEach(file -> {
            String contentType = file.getContentType();
            if (contentType.equals("image/png")
                    || contentType.equals("image/jpg")
                    || contentType.equals("image/jpeg")) {
                fileNamesWithSuccess.add(file.getOriginalFilename());

                int length = 10;
                boolean useLetters = true;
                boolean useNumbers = true;

                try {
                    String fileName = file.getOriginalFilename();
                    String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
                    String final_photo_name = generatedString + fileName;
                    String absolute_fileLocation = AppUtil.getPhotoUploadPath(final_photo_name, PHOTOS_FOLDER_NAME,
                            album_id);
                    Path path = Paths.get(absolute_fileLocation);
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    Photo photo = new Photo();
                    photo.setName(fileName);
                    photo.setFilename(final_photo_name);
                    photo.setOriginalFilename(fileName);
                    photo.setAlbum(album);
                    photoService.save(photo);

                    BufferedImage thumbImg = AppUtil.getThumbnail(file, THUMBNAIL_WIDTH);
                    File thumbnail_location = new File(
                            AppUtil.getPhotoUploadPath(final_photo_name, THUMBNAIL_FOLDER_NAME, album_id));
                    ImageIO.write(thumbImg, file.getContentType().split("/")[1], thumbnail_location);
                } catch (Exception e) {
                    // TODO: handle exception
                    log.debug(AlbumError.PHOTO_UPLOAD_ERROR.toString() + ": " + e.getMessage());
                    fileNamesWithError.add(file.getOriginalFilename());
                }

            } else {
                fileNamesWithError.add(file.getOriginalFilename());
            }

        });
        HashMap<String, List<String>> result = new HashMap<>();
        result.put("SUCCESS", fileNamesWithSuccess);
        result.put("ERRORS", fileNamesWithError);

        List<HashMap<String, List<String>>> response = new ArrayList<>();
        response.add(result);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/{album_id}/photos/{photo_id}/download-photo")
    @SecurityRequirement(name = "Vinesh-demo-api")
    public ResponseEntity<?> downloadphoto(@PathVariable("album_id") long album_id,
            @PathVariable("photo_id") long photo_id, Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalaccount = accountService.findByEmail(email);
        Account account = optionalaccount.get();
        Optional<Album> optionalAlbum = albumService.findById(album_id);
        Album album;
        if (optionalAlbum.isPresent()) {
            album = optionalAlbum.get();
            if (account.getId() != album.getAccount().getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Optional<Photo> optionalphoto = photoService.findbyId(photo_id);
        if (optionalphoto.isPresent()) {
            Photo photo = optionalphoto.get();
            Resource resource = null;
            try {
                resource = AppUtil.getFileAsResource(photo.getFilename(), PHOTOS_FOLDER_NAME, album_id);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }
            if (resource == null) {
                return new ResponseEntity<>("File Not Found", HttpStatus.NOT_FOUND);
            }
            String contentType = "application/octet-stream";
            String header = "attachment; filename=\"" + photo.getOriginalFilename() + "\"";
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, header)
                    .body(resource);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/{album_id}")
    @Operation(summary = "Get Album with id")
    @SecurityRequirement(name = "Vinesh-demo-api")
    public ResponseEntity<AlbumPayloadViewDTO> getAlbum(@PathVariable long album_id, Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalaccount = accountService.findByEmail(email);
        Account account = optionalaccount.get();
        Optional<Album> optionalalbum = albumService.findById(album_id);

        Album album;
        if (optionalalbum.isPresent()) {
            album = optionalalbum.get();
            if (account.getId() != album.getAccount().getId())
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        List<PhotoDto> photoDtos = new ArrayList<>();
        for (Photo photo : photoService.findByAlbum_id(album.getId())) {
            String link = "/" + album.getId() + "/photos/" + photo.getId() + "/download-photo";
            photoDtos.add(new PhotoDto(photo.getId(), photo.getName(), photo.getDescription(), photo.getFilename(),
                    link));
        }
        AlbumPayloadViewDTO albumViewDto = new AlbumPayloadViewDTO(album.getId(), album.getName(),
                album.getDescription(), photoDtos);
        return ResponseEntity.ok(albumViewDto);
    }

    @PutMapping(value = "/{album_id}/update_album")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Update the album")
    @SecurityRequirement(name = "Vinesh-demo-api")
    public ResponseEntity<AlbumPayloadViewDTO> updatealbum(@RequestBody AlbumPayloadDTO albumPayloadDTO,
            @PathVariable long album_id,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<Account> optionalaccount = accountService.findByEmail(email);
            Account account = optionalaccount.get();
            Optional<Album> optionalalbum = albumService.findById(album_id);
            Album album;
            if (optionalalbum.isPresent()) {
                album = optionalalbum.get();
                if (account.getId() != album.getAccount().getId()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            album.setName(albumPayloadDTO.getName());
            album.setDescription(albumPayloadDTO.getDescription());
            albumService.save(album);
            List<PhotoDto> photoDtos = new ArrayList<>();
            for (Photo photo : photoService.findByAlbum_id(album.getId())) {
                String link = "/" + album.getId() + "/photos/" + photo.getId() + "/download-photo";
                photoDtos.add(new PhotoDto(photo.getId(), photo.getName(), photo.getDescription(), photo.getFilename(),
                        link));
            }
            AlbumPayloadViewDTO albumViewDto = new AlbumPayloadViewDTO(album.getId(), album.getName(),
                    album.getDescription(), photoDtos);
            return ResponseEntity.ok(albumViewDto);
        } catch (Exception e) {
            log.debug(AlbumError.ALBUM_ERROR.toString() + ":" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }

    @PutMapping(value = "/{album_id}/photos/{photo_id}/update_photo")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Update the photo")
    @SecurityRequirement(name = "Vinesh-demo-api")
    public ResponseEntity<PhotoViewDto> updatephoto(@RequestBody PhotoPayloadDto photoPayloadDTO,
            @PathVariable long album_id, @PathVariable long photo_id,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<Account> optionalaccount = accountService.findByEmail(email);
            Account account = optionalaccount.get();
            Optional<Album> optionalalbum = albumService.findById(album_id);
            Album album;
            if (optionalalbum.isPresent()) {
                album = optionalalbum.get();
                if (account.getId() != album.getAccount().getId()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            Optional<Photo> optionalphoto = photoService.findbyId(photo_id);
            Photo photo;
            if (optionalphoto.isPresent()) {
                photo = optionalphoto.get();
                if (photo.getAlbum().getId() != album_id) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
                photo.setName(photoPayloadDTO.getName());
                photo.setDescription(photoPayloadDTO.getDescription());
                photoService.save(photo);
                PhotoViewDto photoViewDto = new PhotoViewDto(photo.getId(), photo.getName(), photo.getDescription());
                return ResponseEntity.ok(photoViewDto);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{album_id}/photos/{photo_id}/delete-photo")
    @SecurityRequirement(name = "Vinesh-demo-api")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Delete Photo with photo_id")
    public ResponseEntity<String> photo_delete(@PathVariable long album_id, @PathVariable long photo_id,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<Account> optionalaccount = accountService.findByEmail(email);
            Account account = optionalaccount.get();
            Optional<Album> optionalalbum = albumService.findById(album_id);
            Album album;
            if (optionalalbum.isPresent()) {
                album = optionalalbum.get();
                if (account.getId() != album.getAccount().getId()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            Optional<Photo> optionalphoto = photoService.findbyId(photo_id);
            if (optionalphoto.isPresent()) {
                Photo photo = optionalphoto.get();
                if (album.getId() != photo.getAlbum().getId()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
                AppUtil.delete_photo(photo.getFilename(), PHOTOS_FOLDER_NAME, album_id);
                AppUtil.delete_photo(photo.getFilename(), THUMBNAIL_FOLDER_NAME, album_id);
                photoService.delete(photo);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }

    @DeleteMapping("/{album_id}/delte-album")
    @SecurityRequirement(name = "Vinesh-demo-api")
    @Operation(summary = "Delete album with id")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<String> album_delete(@PathVariable long album_id, Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<Account> optionalaccount = accountService.findByEmail(email);
            Account account = optionalaccount.get();
            Optional<Album> optionalalbum = albumService.findById(album_id);
            Album album;
            if (optionalalbum.isPresent()) {
                album = optionalalbum.get();
                if (account.getId() != album.getAccount().getId()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            for (Photo photo : photoService.findByAlbum_id(album.getId())) {
                AppUtil.delete_photo(photo.getFilename(), PHOTOS_FOLDER_NAME, album_id);
                AppUtil.delete_photo(photo.getFilename(), THUMBNAIL_FOLDER_NAME, album_id);
                photoService.delete(photo);
            }
            albumService.delete(album);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }

}
