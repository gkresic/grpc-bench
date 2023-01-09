package com.steatoda.grpcbench;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class JsonUtil {

	public static JsonMapper jsonMapper() {
		return JsonMapper.builder()
				   .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
				   .build();
	}

}
