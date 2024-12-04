package com.vinesh.SpringRest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vinesh.SpringRest.model.Album;
import java.util.List;

@Repository
public interface AlbumRepositry extends JpaRepository<Album, Long> {
    List<Album> findByAccount_id(long id);
}
