package com.example.insta_test.models.media;

import com.example.insta_test.models.IGBaseModel;
import com.example.insta_test.models.media.timeline.Comment.Caption;
import com.example.insta_test.models.user.User;

import lombok.Data;

@Data
public class Media extends IGBaseModel {
    private long pk;
    private String id;
    private long taken_at;
    private long device_timestamp;
    private String media_type;
    private String code;
    private String client_cache_key;
    private int filter_type;
    private User user;
    private Caption caption;
    private boolean can_viewer_reshare;
    private boolean photo_of_you;
    private boolean can_viewer_save;
}
