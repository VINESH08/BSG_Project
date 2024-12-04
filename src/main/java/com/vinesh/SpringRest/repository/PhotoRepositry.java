package com.vinesh.SpringRest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vinesh.SpringRest.model.Photo;

@Repository
public interface PhotoRepositry extends JpaRepository<Photo, Long> {
    List<Photo> findByAlbum_id(long id);
}
