package com.example.insta_test.responses.live;

import com.example.insta_test.responses.IGResponse;

import lombok.Data;

@Data
public class LiveBroadcastHeartbeatResponse extends IGResponse {
    private int viewer_count;
    private String broadcast_status;
    private String[] cobroadcaster_ids;
    private int offset_to_video_start;
}
