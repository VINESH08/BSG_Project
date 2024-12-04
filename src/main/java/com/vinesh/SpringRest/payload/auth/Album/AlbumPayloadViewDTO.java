package com.vinesh.SpringRest.payload.auth.Album;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AlbumPayloadViewDTO {
    private long id;
    @NotBlank
    @Schema(description = "Album name", example = "Travel", requiredMode = RequiredMode.REQUIRED)
    private String name;
    @NotBlank
    @Schema(description = "description of album", example = "description", requiredMode = RequiredMode.REQUIRED)
    private String description;
    private List<PhotoDto> photos;
}
