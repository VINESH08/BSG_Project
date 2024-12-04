package com.vinesh.SpringRest.payload.auth.Album;

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
public class PhotoPayloadDto {
    @NotBlank
    @Schema(description = "Photo name", example = "Pic", requiredMode = RequiredMode.REQUIRED)
    private String name;
    @NotBlank
    @Schema(description = "description of Photo", example = "good", requiredMode = RequiredMode.REQUIRED)
    private String description;
}
