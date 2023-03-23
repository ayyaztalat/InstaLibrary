package com.example.insta_test.models.media.timeline;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.example.insta_test.models.media.ImageVersions;

import lombok.Data;

// media_type 1
@Data
@JsonTypeName("1")
public class ImageCarouselItem extends CarouselItem {
    private ImageVersions image_versions2;
}
