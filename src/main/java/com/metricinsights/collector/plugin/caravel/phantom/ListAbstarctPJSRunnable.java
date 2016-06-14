package com.metricinsights.collector.plugin.caravel.phantom;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.metricinsights.collector.plugin.ExternalReportsListResponse;
import com.metricinsights.collector.plugin.caravel.CaravelSettings;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * @author NickVeremeichyk
 * @since 2016-06-13.
 */
public class ListAbstarctPJSRunnable extends AbstractPJSRunnable {

    private ExternalReportsListResponse list;
    private final String LIST_API = "/sliceasync/api/read?_oc_SliceAsync=changed_on&_od_SliceAsync=desc";

    public ListAbstarctPJSRunnable(String url, CaravelSettings settings) {
        this.url = url;
        this.settings = settings;
    }

    @Override
    protected void pullData() throws Exception {
        String fullUrl = String.format("%s%s", url, LIST_API);
        CloseableHttpResponse response = fromWebDriverToHttpClient(fullUrl, settings.getCommand());
        String responseStr = IOUtils.toString(response.getEntity().getContent());
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(responseStr).getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonArray("result");



    }

    public ExternalReportsListResponse getList() {
        return list;
    }
}
