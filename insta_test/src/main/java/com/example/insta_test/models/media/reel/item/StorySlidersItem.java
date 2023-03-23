package com.example.insta_test.models.media.reel.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@JsonInclude(Include.NON_NULL)
public class StorySlidersItem extends ReelMetadataItem {
    @NonNull
    private String question;
    @Builder.Default
    private String emoji = "\uD83D\uDE0D";
    @Builder.Default
    private String text_color = "#7F007F";
    @Builder.Default
    private String background_color = "#FFFFFF";

    @Override
    public String key() {
        return "story_sliders";
    }

}
