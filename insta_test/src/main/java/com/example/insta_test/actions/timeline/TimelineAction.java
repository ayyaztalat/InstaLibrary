package com.example.insta_test.actions.timeline;

import com.example.insta_test.IGClient;
import com.example.insta_test.actions.feed.FeedIterable;
import com.example.insta_test.actions.media.MediaAction;
import com.example.insta_test.models.media.UploadParameters;
import com.example.insta_test.requests.feed.FeedTimelineRequest;
import com.example.insta_test.requests.media.MediaConfigureSidecarRequest.MediaConfigureSidecarPayload;
import com.example.insta_test.requests.media.MediaConfigureSidecarRequest.SidecarChildrenMetadata;
import com.example.insta_test.requests.media.MediaConfigureTimelineRequest.MediaConfigurePayload;
import com.example.insta_test.responses.IGResponse;
import com.example.insta_test.responses.feed.FeedTimelineResponse;
import com.example.insta_test.responses.media.MediaResponse.MediaConfigureSidecarResponse;
import com.example.insta_test.responses.media.MediaResponse.MediaConfigureTimelineResponse;
import com.example.insta_test.responses.media.RuploadPhotoResponse;
import com.example.insta_test.utils.IGUtils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public class TimelineAction {
    @NonNull
    private final IGClient client;

    public FeedIterable<FeedTimelineRequest, FeedTimelineResponse> feed() {
        return new FeedIterable<>(client, FeedTimelineRequest::new);
    }

    public CompletableFuture<MediaConfigureTimelineResponse> uploadPhoto(byte[] data,
            MediaConfigurePayload payload) {
        return client.actions().upload()
                .photo(data, String.valueOf(System.currentTimeMillis()))
                .thenCompose(res -> MediaAction.configureMediaToTimeline(client, res.getUpload_id(), payload));
    }

    public CompletableFuture<MediaConfigureTimelineResponse> uploadPhoto(byte[] data,
            String caption) {
        return uploadPhoto(data, new MediaConfigurePayload().caption(caption));
    }

    public CompletableFuture<MediaConfigureTimelineResponse> uploadPhoto(File file,
            String caption) {
        return uploadPhoto(file,
                new MediaConfigurePayload().caption(caption));
    }

    public CompletableFuture<MediaConfigureTimelineResponse> uploadPhoto(File file,
            MediaConfigurePayload payload) {
        try {
            return uploadPhoto(Files.readAllBytes(file.toPath()), payload);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    public CompletableFuture<MediaConfigureTimelineResponse> uploadVideo(byte[] videoData,
                                                                         byte[] coverData,
                                                                         MediaConfigurePayload payload) {
        return uploadVideoWithTimeout(videoData, coverData, payload, 0L);
    }

    public CompletableFuture<MediaConfigureTimelineResponse> uploadVideo(File video, File cover,
            String caption) {
        return uploadVideo(video, cover, new MediaConfigurePayload().caption(caption));
    }

    public CompletableFuture<MediaConfigureTimelineResponse> uploadVideo(File video, File cover,
            MediaConfigurePayload payload) {
        try {
            return uploadVideo(Files.readAllBytes(video.toPath()),
                    Files.readAllBytes(cover.toPath()), payload);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    /**
     * Upload video with timeout before upload finish request
     *
     * @param videoData video file bytes
     * @param coverData cover file bytes
     * @param payload media payload
     * @param uploadFinishTimeoutSeconds timeout before upload finish in seconds
     * @return completable future of MediaConfigureTimelineResponse
     */
    public CompletableFuture<MediaConfigureTimelineResponse> uploadVideoWithTimeout(byte[] videoData,
                                                                                    byte[] coverData,
                                                                                    MediaConfigurePayload payload,
                                                                                    long uploadFinishTimeoutSeconds) {
        String upload_id = String.valueOf(System.currentTimeMillis());
        return client.actions().upload()
                .videoWithCover(videoData, coverData,
                        UploadParameters.forTimelineVideo(upload_id, false))
                .thenCompose(response -> {
                    IGUtils.sleepSeconds(uploadFinishTimeoutSeconds);

                    return client.actions().upload().finish(upload_id);
                })
                .thenCompose(response -> MediaAction.configureMediaToTimeline(client, upload_id, payload));
    }

    public CompletableFuture<MediaConfigureTimelineResponse> uploadVideo(byte[] videoData,
            byte[] coverData, String caption) {
        return uploadVideo(videoData, coverData, new MediaConfigurePayload().caption(caption));
    }

    public CompletableFuture<MediaConfigureSidecarResponse> uploadAlbum(List<SidecarInfo> infos,
            MediaConfigureSidecarPayload payload) {
        List<CompletableFuture<?>> uploadFutures =
                infos.stream().map(s -> s.upload(client)).collect(Collectors.toList());
        payload.children_metadata()
                .addAll(infos.stream().map(SidecarInfo::metadata).collect(Collectors.toList()));
        return CompletableFuture
                .allOf(uploadFutures.toArray(new CompletableFuture[uploadFutures.size()]))
                .thenCompose(res -> MediaAction.configureAlbumToTimeline(client, payload));
    }

    public CompletableFuture<MediaConfigureSidecarResponse> uploadAlbum(List<SidecarInfo> infos,
            String caption) {
        return uploadAlbum(infos, new MediaConfigureSidecarPayload().caption(caption));
    }

    public static interface SidecarInfo {
        CompletableFuture<? extends IGResponse> upload(IGClient client);

        SidecarChildrenMetadata metadata();
    }

    @Data
    @Accessors(fluent = true, chain = true)
    @Setter
    public static class SidecarPhoto implements SidecarInfo {
        @NonNull
        private byte[] data;
        private SidecarChildrenMetadata metadata =
                new SidecarChildrenMetadata(String.valueOf(System.currentTimeMillis()));

        @Override
        public CompletableFuture<RuploadPhotoResponse> upload(IGClient client) {
            return client.actions().upload().photo(data, metadata.upload_id(), null, true);
        }

        public static SidecarPhoto from(File file) {
            try {
                return new SidecarPhoto(Files.readAllBytes(file.toPath()));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        public static SidecarPhoto from(File file, SidecarChildrenMetadata metadata) {
            try {
                return new SidecarPhoto(Files.readAllBytes(file.toPath())).metadata(metadata);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    @Data
    @Accessors(fluent = true, chain = true)
    @Setter
    public static class SidecarVideo implements SidecarInfo {
        @NonNull
        private byte[] data;
        @NonNull
        private byte[] cover_data;
        private SidecarChildrenMetadata metadata =
                new SidecarChildrenMetadata(String.valueOf(System.currentTimeMillis()));

        @Override
        public CompletableFuture<? extends IGResponse> upload(IGClient client) {
            return client.actions().upload().videoWithCover(data, cover_data,
                    UploadParameters.forTimelineVideo(metadata.upload_id(), true));
        }

        public static SidecarVideo from(File video, File cover) {
            try {
                return new SidecarVideo(Files.readAllBytes(video.toPath()),
                        Files.readAllBytes(cover.toPath()));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        public static SidecarVideo from(File video, File cover, SidecarChildrenMetadata metadata) {
            try {
                return new SidecarVideo(Files.readAllBytes(video.toPath()),
                        Files.readAllBytes(cover.toPath())).metadata(metadata);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
