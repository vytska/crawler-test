package vytautas.com.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import vytautas.com.dtos.CreateJobRequest;
import vytautas.com.dtos.FinishJobRequest;
import vytautas.com.dtos.UpdateListRequest;

import java.io.IOException;
import java.util.Arrays;

public class CrawlerRunner {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();

    private ObjectMapper mapper = new ObjectMapper();

    private String apiPath = "http://localhost:8080/famous-people-job";

    public void runFiveCrawlers() throws IOException {
        String url1 = "http:/www.example.com/1";
        String url2 = "http:/www.example.com/2";
        String url3 = "http:/www.example.com/3";
        String url4 = "http:/www.example.com/4";
        String url5 = "http:/www.example.com/5";

        createJob(url1);
        updateJob(url1, "Philip Seymour Hoffman", "Matthew McConaughey");
        createJob(url2);
        createJob(url4);
        createJob(url4);
        createJob(url3);
        updateJob(url3, "David bowie");
        updateJob(url4);
        updateJob(url3, "Bob Dilan");
        updateJob(url4, "dramatic hamster");
        updateJob(url1, "Anthony Hopkins");
        finishJob(url4, "rep4");
        finishJob(url4, "rep4");
        updateJob(url5);
        createJob(url5);
        finishJob(url3, "rep3");
        updateJob(url2, "Barack Obama", "Angela Merkel");
    }

    private void createJob(String url) throws IOException {
        CreateJobRequest createJob = new CreateJobRequest(url);
        Request createJobRequest = new Request.Builder()
                .url(apiPath)
                .post(RequestBody.create(JSON, mapper.writeValueAsString(createJob)))
                .build();

        client.newCall(createJobRequest).execute();
    }

    private void updateJob(String url, String... famousPeople) throws IOException {
        UpdateListRequest updateListRequest = new UpdateListRequest(url);
        updateListRequest.setList(Arrays.asList(famousPeople));
        Request createJobRequest = new Request.Builder()
                .url(apiPath + "/list")
                .put(RequestBody.create(JSON, mapper.writeValueAsString(updateListRequest)))
                .build();

        client.newCall(createJobRequest).execute();
    }

    private void finishJob(String url, String repositoryKey) throws IOException {
        FinishJobRequest finishJobRequest = new FinishJobRequest(url, repositoryKey);

        Request createJobRequest = new Request.Builder()
                .url(apiPath)
                .patch(RequestBody.create(JSON, mapper.writeValueAsString(finishJobRequest)))
                .build();

        client.newCall(createJobRequest).execute();
    }
}
