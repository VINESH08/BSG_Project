package com.vinesh.SpringRest.payload.auth.Album;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhotoDto {
    private long id;
    private String name;
    private String description;
    private String filename;
    private String downloadlink;
}
