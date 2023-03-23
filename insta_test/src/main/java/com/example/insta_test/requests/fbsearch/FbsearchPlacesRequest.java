package com.example.insta_test.requests.fbsearch;

import com.example.insta_test.IGClient;
import com.example.insta_test.requests.IGGetRequest;
import com.example.insta_test.requests.IGPageRankTokenRequest;
import com.example.insta_test.responses.fbsearch.FbsearchPlacesResponse;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
public class FbsearchPlacesRequest extends IGGetRequest<FbsearchPlacesResponse> implements IGPageRankTokenRequest {
    @NonNull
    private String query;
    private Double lat, lon;
    @Setter
    private String page_token, rank_token;
    private final int count = 30;

    @Override
    public String path() {
        return "fbsearch/places";
    }

    @Override
    public String getQueryString(IGClient client) {

        return mapQueryString(
                "search_surface", "places_search_page",
                "timezone_offset", "0",
                "query", query,
                "lat", lat,
                "lng", lon,
                "count", count,
                "rank_token", rank_token,
                "page_token", page_token);
    }

    @Override
    public Class<FbsearchPlacesResponse> getResponseType() {
        return FbsearchPlacesResponse.class;
    }

}
