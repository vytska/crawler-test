package vytautas.com;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.http.HttpStatus;
import vytautas.com.dtos.CreateJobRequest;
import vytautas.com.dtos.FamousPeopleJobDto;
import vytautas.com.dtos.FinishJobRequest;
import vytautas.com.dtos.UpdateListRequest;
import vytautas.com.services.JobTracker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ApiTests {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();

    private ObjectMapper mapper = new ObjectMapper();

    private String hostname = "http://localhost:8080/";

    private String famousPeopleJobPath = "famous-people-jobs";

    private JobTracker jobTracker = new JobTracker();


    @BeforeClass
    public static void runApp() {
        SpringApplication.run(AppRunner.class);
    }

    @After
    public void shutDownApp() throws InterruptedException {
        jobTracker.clearJobs();
    }

    /**
     * Specification (1) a
     * Crawler sends a source URL, which is about to be scanned.
     */
    @Test
    public void testJobGetsCreated() throws IOException {
        String jobUrl = encodeUrl("http://some-url.com");
        createJob(jobUrl);
    }

    private String encodeUrl(String url) throws UnsupportedEncodingException {
        return URLEncoder.encode(url, "UTF-8");
    }

    private void createJob(String jobUrl) throws IOException {
        CreateJobRequest createJob = new CreateJobRequest(jobUrl);
        Response createResp = makePostCall(famousPeopleJobPath, createJob);
        assertThat(createResp.code(), is(HttpStatus.OK.value()));
        checkJobAfterInitialCreation(jobUrl);
    }

    private Response makePostCall(String path, Object body) throws IOException {
        return makePostCallWithJson(path, mapper.writeValueAsString(body));
    }

    private Response makePostCallWithJson(String path, String json) throws IOException {
        Request createJobRequest = new Request.Builder()
                .url(hostname + path)
                .post(RequestBody.create(JSON, json))
                .build();

        return client.newCall(createJobRequest).execute();
    }

    private void checkJobAfterInitialCreation(String url) throws IOException {
        FamousPeopleJobDto job = searchParticularJob(url);

        assertThat(job.getUrl(), is(url));
        assertThat(job.getFamousPeople().size(), is(0));
        assertThat(job.getRepositoryKey(), is(nullValue()));
    }

    private FamousPeopleJobDto searchParticularJob(String jobUrl) throws IOException {
        Request findJobRequest = new Request.Builder()
                .url(hostname + famousPeopleJobPath + "?url=" + URLEncoder.encode(jobUrl, "UTF-8"))
                .get()
                .build();

        Response response = client.newCall(findJobRequest).execute();
        List<FamousPeopleJobDto> famousPeople = mapper.readValue(response.body().string(), new TypeReference<List<FamousPeopleJobDto>>() {
        });

        assertThat(famousPeople.size(), is(1));
        return famousPeople.get(0);
    }

    /**
     * Specification (1) a
     * In case the URL was already transmitted, please return a warning response code
     */
    @Test
    public void testSameUrlCreationThrowsError() throws IOException {
        String jobUrl = encodeUrl("http://www.example.com?param=value");
        createJob(jobUrl);

        Response createResp2 = makePostCall(famousPeopleJobPath, new CreateJobRequest(jobUrl));
        assertThat(createResp2.code(), is(HttpStatus.ALREADY_REPORTED.value()));

        checkJobAfterInitialCreation(jobUrl);
    }


    @Test
    public void testJobGetsCreatedWithJson() throws IOException {
        String jobUrl = encodeUrl("just string");

        CreateJobRequest createJob = new CreateJobRequest();
        createJob.setUrl(jobUrl);

        Response createResp = makePostCallWithJson(famousPeopleJobPath, "{\"url\": \"" + jobUrl + "\"}");
        assertThat(createResp.code(), is(HttpStatus.OK.value()));

        checkJobAfterInitialCreation(jobUrl);
    }

    @Test
    public void testNoUrlReturnsError() throws IOException {
        Response createResp = makePostCall(famousPeopleJobPath, new CreateJobRequest());
        assertThat(createResp.code(), is(HttpStatus.BAD_REQUEST.value()));
    }

    /**
     * Specification (1) b
     * Crawler sends a list of extracted famous people for a specific URL.
     */
    @Test
    public void testFamousPeopleListGetsSaved() throws IOException {
        String jobUrl = encodeUrl("http://www.example.com?param=value");
        createJob(jobUrl);

        List<String> famousPeople = new ArrayList<>();
        famousPeople.add("Philip Seymour Hoffman");
        famousPeople.add("Matthew McConaughey");
        famousPeople.add("That guy from Titanic");

        Response updateResp = makeUpdateJobCall(jobUrl, famousPeople);
        assertThat(updateResp.code(), is(HttpStatus.OK.value()));

        FamousPeopleJobDto job = searchParticularJob(jobUrl);

        assertThat(job.getUrl(), is(jobUrl));
        assertThat(job.getFamousPeople(), contains(famousPeople.toArray()));
    }


    private Response makeUpdateJobCall(String jobUrl, List<String> famousPeople) throws IOException {
        UpdateListRequest updateListRequest = new UpdateListRequest(famousPeople);
        Request createJobRequest = new Request.Builder()
                .url(buildUrl(jobUrl, "append"))
                .patch(RequestBody.create(JSON, mapper.writeValueAsString(updateListRequest)))
                .build();

        return client.newCall(createJobRequest).execute();
    }

    private HttpUrl buildUrl(String jobUrl, String additionalSegment) throws UnsupportedEncodingException {
        return new HttpUrl.Builder()
                .scheme("http")
                .host("localhost")
                .port(8080)
                .addPathSegment(famousPeopleJobPath)
                .addPathSegment(jobUrl)
                .addPathSegment(additionalSegment)
                .build();
    }

    @Test
    public void testFamousPeopleListGetsAppended() throws IOException {
        String jobUrl = encodeUrl("http://www.example.com?param=value");
        createJob(jobUrl);

        List<String> famousPeople = new ArrayList<>();
        famousPeople.add("Philip Seymour Hoffman");
        famousPeople.add("Matthew McConaughey");

        makeUpdateJobCall(jobUrl, famousPeople);

        List<String> additionalFamousPeople = new ArrayList<>();
        additionalFamousPeople.add("dramatic hamster");
        additionalFamousPeople.add("Santa");
        famousPeople.addAll(additionalFamousPeople);

        Response updateResp = makeUpdateJobCall(jobUrl, additionalFamousPeople);
        assertThat(updateResp.code(), is(HttpStatus.OK.value()));

        FamousPeopleJobDto job = searchParticularJob(jobUrl);

        assertThat(job.getUrl(), is(jobUrl));
        assertThat(job.getFamousPeople(), contains(famousPeople.toArray()));
    }

    /**
     * Specification (1) b
     * An unknown URL is any URL which was not received in call #1a and shall return an error for your service.
     */
    @Test
    public void testFamousPeopleListUrlNotFoundException() throws IOException {
        String jobUrl = encodeUrl("http://www.example.com?param=value");
        createJob(jobUrl);

        Response updateResp = makeUpdateJobCall(jobUrl + "!", Collections.emptyList());
        assertThat(updateResp.code(), is(HttpStatus.NOT_FOUND.value()));
    }


    /**
     * Specification (1) c
     * Crawler sends a remote repository key (string) for a specific URL.
     */
    @Test
    public void testRepositoryUriGetsSaved() throws IOException {
        String jobUrl = encodeUrl("http://www.example.com?param=value");
        String repositoryKey = "repKey";
        createJob(jobUrl);



        Response updateResp = makeFinishJobCall(jobUrl, repositoryKey);
        assertThat(updateResp.code(), is(HttpStatus.OK.value()));

        FamousPeopleJobDto job = searchParticularJob(jobUrl);
        assertThat(job.getRepositoryKey(), is(repositoryKey));
    }

    private Response makeFinishJobCall(String jobUrl, String repositoryKey) throws IOException {
        FinishJobRequest finishJobRequest = new FinishJobRequest(repositoryKey);
        Request createJobRequest = new Request.Builder()
                .url(buildUrl(jobUrl, "finish"))
                .patch(RequestBody.create(JSON, mapper.writeValueAsString(finishJobRequest)))
                .build();

        return client.newCall(createJobRequest).execute();
    }

    /**
     * Specification (1) c
     * Again an unknown URL is an error.
     */
    @Test
    public void testUrlNotFoundOnFinish() throws IOException {
        String jobUrl = encodeUrl("http://www.example.com?param=value");
        String repositoryKey = "repKey";
        createJob(jobUrl);

        Response updateResp = makeFinishJobCall(jobUrl + "1", repositoryKey);
        assertThat(updateResp.code(), is(HttpStatus.NOT_FOUND.value()));
    }

    /**
     * Specification (1) c
     * This is the last modification call for the job and marks the end of it.
     */
    @Test
    public void testAfterJobFinishesNoUpdatesAreAllowed() throws IOException {
        String jobUrl = encodeUrl("http://www.example.com?param=value");
        String repositoryKey = "repKey";
        createJob(jobUrl);

        Response updateResp = makeFinishJobCall(jobUrl, repositoryKey);
        assertThat(updateResp.code(), is(HttpStatus.OK.value()));

        List<String> famousPeople = new ArrayList<>();
        famousPeople.add("Some Guy");

        Response listResp = makeUpdateJobCall(jobUrl, famousPeople);
        assertThat(listResp.code(), is(HttpStatus.UNPROCESSABLE_ENTITY.value()));
    }

    /**
     * Specification (2) a
     * Crawler might ask to retrieve any information about given URL - a current list of famous people and the repository key.
     */
    @Test
    public void testGetParticularJob() throws IOException {
        String jobUrl = encodeUrl("http://www.example.com?param=value");
        String repositoryKey = "repKey";
        createJob(jobUrl);


        List<String> famousPeople = new ArrayList<>();
        famousPeople.add("Philip Seymour Hoffman");

        Response updateResp = makeUpdateJobCall(jobUrl, famousPeople);
        assertThat(updateResp.code(), is(HttpStatus.OK.value()));

        Response finishResp = makeFinishJobCall(jobUrl, repositoryKey);
        assertThat(finishResp.code(), is(HttpStatus.OK.value()));


        FamousPeopleJobDto job = searchParticularJob(jobUrl);

        assertThat(job.getUrl(), is(jobUrl));
        assertThat(job.getRepositoryKey(), is(repositoryKey));
        assertThat(job.getFamousPeople(), contains(famousPeople.toArray()));
    }

    /**
     * Specification (2) b
     * Crawler might ask to retrieve any jobs, which are not finished at the moment.
     */
    @Test
    public void testUnfinishedJobsRetrieval() throws IOException {
        String job1Url = encodeUrl("http://www.example.com?param=value");
        String job2Url = encodeUrl("http://www.example.com?param=other value");
        String job3Url = encodeUrl("http://exampleelse.com");
        String repositoryKey = "repKey";
        createJob(job1Url);
        createJob(job2Url);
        createJob(job3Url);


        List<String> famousPeople = new ArrayList<>();
        famousPeople.add("Philip Seymour Hoffman");

        Response updateResp = makeUpdateJobCall(job1Url, famousPeople);
        assertThat(updateResp.code(), is(HttpStatus.OK.value()));

        Response finishResp = makeFinishJobCall(job1Url, repositoryKey);
        assertThat(finishResp.code(), is(HttpStatus.OK.value()));


        List<FamousPeopleJobDto> jobs = searchUnfinishedJobs();
        assertThat(jobs.size(), is(2));
        assertThat(jobs, containsInAnyOrder(hasProperty("url", is(job2Url)), hasProperty("url", is(job3Url))
        ));
    }

    private List<FamousPeopleJobDto> searchUnfinishedJobs() throws IOException {
        Request findJobRequest = new Request.Builder()
                .url(hostname + famousPeopleJobPath + "?state=unfinished")
                .get()
                .build();

        Response response = client.newCall(findJobRequest).execute();
        return mapper.readValue(response.body().string(), new TypeReference<List<FamousPeopleJobDto>>() {});
    }

    /**
     * Specification (3) a
     * An URL path to check the service uptime and health
     * Specification (3) c
     * Metrics for durations and invocation counts for business calls, dumped to console or separate file.
     * (Implementation alteration - instead of file the data is exposed through rest as well)
     */
    @Test
    public void testMetrics() throws IOException {
        createJob("example");
        searchUnfinishedJobs();

        Request metricsRequest = new Request.Builder()
                .url(hostname + "custom-metrics")
                .get().build();

        Response response = client.newCall(metricsRequest).execute();

        Map<String, Object> metrics = mapper.readValue(response.body().string(), new TypeReference<Map<String, Object>>() {});

        assertThat(metrics, hasKey("gauge.famous-people-jobs-create.max"));
        assertThat(metrics, hasKey("gauge.famous-people-jobs-create.min"));
        assertThat(metrics, hasKey("gauge.famous-people-jobs-create.average"));
        assertThat(metrics, hasKey("gauge.famous-people-jobs-create.last"));

        assertThat(metrics, hasKey("gauge.famous-people-jobs-search.max"));
        assertThat(metrics, hasKey("gauge.famous-people-jobs-search.min"));
        assertThat(metrics, hasKey("gauge.famous-people-jobs-search.average"));
        assertThat(metrics, hasKey("gauge.famous-people-jobs-search.last"));
        assertThat(metrics, hasKey("counter.famous-people-jobs-create"));
        assertThat(metrics, hasKey("counter.famous-people-jobs-search"));

        assertThat(metrics, hasKey("uptime"));



        Map<String, Object> health = (Map<String, Object>)metrics.get("health");
        assertThat(health.size(), is(1));
        assertThat(health, hasEntry("status", "UP"));

    }

}
