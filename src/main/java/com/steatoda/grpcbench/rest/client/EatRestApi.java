package com.steatoda.grpcbench.rest.client;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface EatRestApi {

	@POST("eat")
	Call<ResponseBody> eat(@Body RequestBody body);

}
