package com.example.insta_test.models.media.timeline;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

import lombok.Data;

@Data
@JsonTypeName("8")
public class TimelineCarouselMedia extends TimelineMedia {
    private int carousel_media_count;
    private List<CarouselItem> carousel_media;
}
