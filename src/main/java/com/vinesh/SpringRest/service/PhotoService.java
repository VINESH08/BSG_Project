package com.vinesh.SpringRest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vinesh.SpringRest.model.Photo;
import com.vinesh.SpringRest.repository.PhotoRepositry;

@Service
public class PhotoService {
    @Autowired
    private PhotoRepositry photoRepositry;

    public Photo save(Photo photo) {
        return photoRepositry.save(photo);
    }

    public Optional<Photo> findbyId(long id) {
        return photoRepositry.findById(id);
    }

    public List<Photo> findByAlbum_id(long id) {
        return photoRepositry.findByAlbum_id(id);
    }

    public void delete(Photo photo) {
        photoRepositry.delete(photo);
    }
}
