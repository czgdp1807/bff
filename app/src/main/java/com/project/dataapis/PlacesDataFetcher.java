package com.project.dataapis;

import android.os.AsyncTask;
import android.util.Pair;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.project.config.GoogleAPIKeys;
import com.project.config.GoogleURLs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class PlacesDataFetcher extends AsyncTask<HashMap<String, String>, Boolean, Integer> implements DataFetcherInterface
{

    private String APIKey;
    private JsonNode data;
    private String placeURL;
    private Integer responseCode;
    private String IOExceptioError;

    public PlacesDataFetcher()
    {
        APIKey = "";
        data = null;
        responseCode = 404;
        IOExceptioError = "";
        placeURL = "";
    }

    @Override
    public void setAPIKey()
    {
        APIKey = GoogleAPIKeys.Places;
    }

    private void fetchData(HashMap<String, String> queryMap)
    {
        try
        {

            Pair<JsonNode, Integer> results = Util.fetchData(queryMap, APIKey, GoogleURLs.Places);
            data = results.first;
            responseCode = results.second;
            if( responseCode == 200 && data.has("results") &&
                data.get("results").isArray() && data.get("results").size() > 0)
            {
                ArrayNode items = (ArrayNode) data.get("results");
                Integer resultsPerPage = items.size();
                Random random = new Random();
                Integer resultIdx = random.nextInt(resultsPerPage);
                JsonNode result = items.get(resultIdx);
                HashMap<String, String> urlPlaceQuery = new HashMap<String, String>();
                urlPlaceQuery.put("place_id", result.get("place_id").toString()
                                                    .replaceAll("\"", ""));
                urlPlaceQuery.put("fields", "url");
                results = Util.fetchData(urlPlaceQuery, APIKey, GoogleURLs.URLPlaces);
                if( results.second == 200 && results.first.has("result") )
                {
                    placeURL = results.first.get("result").get("url").toString();
                }
                data = result;
            }
        }
        catch (IOException e)
        {
            IOExceptioError = e.toString();
        }
    }

    @Override
    protected Integer doInBackground(HashMap<String, String>... hashMaps)
    {
        fetchData(hashMaps[0]);
        return responseCode;
    }

    @Override
    public HashMap<String, String> toHashMap()
    {
        HashMap<String, String> resultInfo = new HashMap<String, String>();
        resultInfo.put("name", "No Name available.");
        resultInfo.put("address", "No Address available.");
        resultInfo.put("vicinity", "No Vicinity Available");
        resultInfo.put("url", "No URL available");
        if( responseCode == 200 && data.has("name") &&
            data.has("plus_code") && data.has("vicinity") )
        {
            resultInfo.put("name", data.get("name").toString().replaceAll("\"", ""));
            resultInfo.put("address", data.get("plus_code").get("compound_code")
                                          .toString().replaceAll("\"", ""));
            resultInfo.put("vicinity", data.get("vicinity").toString()
                                           .replaceAll("\"", ""));
            if( placeURL != "" )
            {
                resultInfo.put("url", placeURL.replaceAll("\"", ""));
            }
        }
        return resultInfo;
    }
}
