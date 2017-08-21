package com.skyver.rssnews.retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

import static com.skyver.rssnews.util.Constants.GET_RSS;

/**
 * Created by skyver on 8/19/17.
 */

public interface Restapi {

    @GET(GET_RSS)
    Call<RSSMain> loadRSSFeed();

}

