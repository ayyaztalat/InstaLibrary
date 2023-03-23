package com.example.insta_test.models.media.reel;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.example.insta_test.models.media.ImageMedia;
import com.example.insta_test.models.media.ImageVersions;

import lombok.Data;

@Data
@JsonTypeName("1")
public class ReelImageMedia extends ReelMedia implements ImageMedia {
    private ImageVersions image_versions2;
}
