/*
 * Copyright 2016 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.notification.executors;

import com.example.notification.PluginRequest;
import com.example.notification.RequestExecutor;
import com.example.notification.requests.StageStatusRequest;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.util.Arrays;
import java.util.HashMap;

public class StageStatusRequestExecutor implements RequestExecutor {

    private static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    private final StageStatusRequest request;
    private final PluginRequest pluginRequest;

    public StageStatusRequestExecutor(StageStatusRequest request, PluginRequest pluginRequest) {
        this.request = request;
        this.pluginRequest = pluginRequest;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        HashMap<String, Object> responseJson = new HashMap<>();
        try {
            sendNotification();
            responseJson.put("status", "success");
//            responseJson.put("message", "complete");
        } catch (Exception e) {
            responseJson.put("status", "failure");
            responseJson.put("message", e.getMessage());
            // responseJson.put("messages", Arrays.asList(e.getMessage()));
        }
        return new DefaultGoPluginApiResponse(200, GSON.toJson(responseJson));
    }

    protected void sendNotification() throws Exception {
        // TODO: Implement this. The request.pipeline object has all the details about the pipeline, materials, stages and jobs
        // If you need access to settings like API keys, URLs, then call PluginRequest#getPluginSettings
        // PluginSettings pluginSettings = pluginRequest.getPluginSettings();

        this.postStageToApi();
    }

    private void postStageToApi() throws Exception {

        // throw new PostStageToApiException();
        
        String json = GSON.toJson(this.request);
        String encodedData = URLEncoder.encode(json, "UTF-8");
        URL url = new URL("http://localhost:3000/api/notifications/stage-status");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Content-Length", String.valueOf(encodedData.length()));
        OutputStream os = conn.getOutputStream();
        os.write(encodedData.getBytes());
    }
}
