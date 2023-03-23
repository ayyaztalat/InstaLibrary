package com.example.insta_test.models.live;

import com.example.insta_test.models.IGBaseModel;
import com.example.insta_test.models.user.User;

import lombok.Data;

@Data
public class Broadcast extends IGBaseModel {
    private String id;
    private String dash_playback_url;
    private String dash_abr_playback_url;
    private String dash_live_predictive_playback_url;
    private String broadcast_status;
    private int viewer_count;
    private String cover_frame_url;
    private User broadcast_owner;
    private long published_time;
    private String media_id;
    private String broadcast_message;
}
