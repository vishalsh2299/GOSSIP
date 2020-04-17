package com.example.gossip.Fragments;

import com.example.gossip.Notifications.MyResponse;
import com.example.gossip.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA3q0owbo:APA91bGWEYxeYH2M_NY_f4NhoOVOFIibXoLMMaerBjK0t4rRg1iWzFhmenuhoxJOSR6L8l650cJ2NsXefDG5ZfMHgpG98XdEyBVon_Ap6wuYPBUwSNVPORd0g1tdSugGIqrdhit7LqQD"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
