package com.project.dataapis;

import android.util.Pair;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Util
{
    public static Pair<JsonNode, Integer> fetchData(HashMap<String, String> queryMap, String APIKey, String queryURL)
    throws IOException
    {
        queryMap.put("key", APIKey);
        String stringQuery = toString(queryMap);
        URL url = new URL(queryURL + "?" + stringQuery);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode data = objectMapper.readTree(connection.getInputStream());
        Integer responseCode = connection.getResponseCode();
        return new Pair<JsonNode, Integer>(data, responseCode);

    }

    private static String toString(HashMap<String, String> queryMap)
    {
        StringBuilder stringQueryBuilder = new StringBuilder();
        String prefix = "";
        for( Map.Entry<String, String> query : queryMap.entrySet() )
        {
            String key = query.getKey(), value = query.getValue();
            stringQueryBuilder.append(prefix + key + "=" + value);
            prefix = "&";
        }
        String stringQuery = stringQueryBuilder.toString();
        String formattedStringQuery = stringQuery.replaceAll(" ", "%20");
        return formattedStringQuery;
    }
}
