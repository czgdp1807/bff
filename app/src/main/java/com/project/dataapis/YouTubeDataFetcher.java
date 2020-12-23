package  com.project.dataapis;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.project.config.GoogleAPIKeys;
import com.project.config.GoogleURLs;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class YouTubeDataFetcher extends AsyncTask<HashMap<String, String>, Boolean, Integer> implements DataFetcherInterface
{

    private String APIKey;
    private JsonNode data;
    private Integer responseCode;
    private String IOExceptioError;

    public YouTubeDataFetcher()
    {
        APIKey = "";
        data = null;
        responseCode = 404;
        IOExceptioError = "";
    }

    @Override
    public void setAPIKey()
    {
        APIKey = GoogleAPIKeys.YouTube;
    }

    private void fetchData(HashMap<String, String> queryMap)
    {
        try
        {
            queryMap.put("key", APIKey);
            String queryURL = GoogleURLs.YouTube;
            String stringQuery = toString(queryMap);
            URL url = new URL(queryURL + "?" + stringQuery);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            ObjectMapper objectMapper = new ObjectMapper();
            data = objectMapper.readTree(connection.getInputStream());
            responseCode = connection.getResponseCode();
        }
        catch (IOException e)
        {
            IOExceptioError = e.toString();
        }
    }

    private String toString(HashMap<String, String> queryMap)
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

    public HashMap<String, String> toHashMap()
    {
        HashMap<String, String> resultInfo = new HashMap<String, String>();
        resultInfo.put("url", "No URL available.");
        resultInfo.put("title", "No Title available.");
        if( responseCode == 200 )
        {
            ArrayNode items = (ArrayNode) data.get("items");
            Integer resultsPerPage = data.get("pageInfo").get("resultsPerPage").asInt();
            Random random = new Random();
            Integer resultIdx = random.nextInt(resultsPerPage);
            JsonNode result = items.get(resultIdx);
            resultInfo.put("url", "https://www.youtube.com/watch?v=" + result.get("id").get("videoId")
                                                                             .toString().replaceAll("\"", ""));
            resultInfo.put("title", result.get("snippet").get("title")
                                          .toString().replaceAll("\"", ""));
        }
        return resultInfo;
    }

    @Override
    protected Integer doInBackground(HashMap<String, String>... hashMaps)
    {
        fetchData(hashMaps[0]);
        return responseCode;
    }
}