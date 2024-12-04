package com.vinesh.SpringRest.service;

import java.util.List;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vinesh.SpringRest.model.Album;
import com.vinesh.SpringRest.repository.AlbumRepositry;

@Service
public class AlbumService {
    @Autowired
    private AlbumRepositry albumRepositry;

    public Album save(Album album) {
        return albumRepositry.save(album);
    }

    public List<Album> findByAccount_id(long id) {
        return albumRepositry.findByAccount_id(id);
    }

    public Optional<Album> findById(long id) {
        return albumRepositry.findById(id);
    }

    public void delete(Album album) {
        albumRepositry.delete(album);
    }
}
