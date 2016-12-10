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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

public class FamousPeopleJobsCrawlerApiTests {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();

    private ObjectMapper mapper = new ObjectMapper();

    private String hostname = "http://localhost:8080/";

    private String famousPeopleJobPath = "famous-people-job";

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
        String url = "http://some-url.com";
        createJob(url);
    }

    private void createJob(String url) throws IOException {
        CreateJobRequest createJob = new CreateJobRequest(url);
        Response createResp = makePostCall(famousPeopleJobPath, createJob);
        assertThat(createResp.code(), is(HttpStatus.OK.value()));
        checkJobAfterInitialCreation(url);
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
        String url = "http://something.com?param=value";
        createJob(url);

        Response createResp2 = makePostCall(famousPeopleJobPath, new CreateJobRequest(url));
        assertThat(createResp2.code(), is(HttpStatus.ALREADY_REPORTED.value()));

        checkJobAfterInitialCreation(url);
    }


    @Test
    public void testJobGetsCreatedWithJson() throws IOException {
        String url = "just string";//I thought of validating URL, but that would decrease flexibility

        CreateJobRequest createJob = new CreateJobRequest();
        createJob.setUrl(url);

        Response createResp = makePostCallWithJson(famousPeopleJobPath, "{\"url\": \"" + url + "\"}");
        assertThat(createResp.code(), is(HttpStatus.OK.value()));

        checkJobAfterInitialCreation(url);
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
        String url = "http://something.com?param=value";
        createJob(url);

        UpdateListRequest updateListRequest = new UpdateListRequest(url);
        List<String> famousPeople = new ArrayList<>();
        famousPeople.add("Philip Seymour Hoffman");
        famousPeople.add("Matthew McConaughey");
        famousPeople.add("That guy from Titanic");

        updateListRequest.setList(famousPeople);

        Response updateResp = makePutCall(famousPeopleJobPath + "/list", updateListRequest);
        assertThat(updateResp.code(), is(HttpStatus.OK.value()));

        FamousPeopleJobDto job = searchParticularJob(url);

        assertThat(job.getUrl(), is(url));
        assertThat(job.getFamousPeople(), contains(famousPeople.toArray()));
    }


    private Response makePutCall(String path, Object body) throws IOException {
        Request createJobRequest = new Request.Builder()
                .url(hostname + path)
                .put(RequestBody.create(JSON, mapper.writeValueAsString(body)))
                .build();

        return client.newCall(createJobRequest).execute();
    }

    /**
     * Specification (1) b
     * An unknown URL is any URL which was not received in call #1a and shall return an error for your service.
     */
    @Test
    public void testFamousPeopleListUrlNotFoundException() throws IOException {
        String url = "http://something.com?param=value";
        createJob(url);

        UpdateListRequest updateListRequest = new UpdateListRequest(url + "!");
        Response updateResp = makePutCall(famousPeopleJobPath + "/list", updateListRequest);
        assertThat(updateResp.code(), is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void testFamousPeopleListUrlRequiredException() throws IOException {
        String url = "http://something.com?param=value";
        createJob(url);

        UpdateListRequest updateListRequest = new UpdateListRequest();
        Response updateResp = makePutCall(famousPeopleJobPath + "/list", updateListRequest);
        assertThat(updateResp.code(), is(HttpStatus.BAD_REQUEST.value()));
    }


    /**
     * Specification (1) c
     * Crawler sends a remote repository key (string) for a specific URL.
     */
    @Test
    public void testRepositoryUriGetsSaved() throws IOException {
        String url = "http://something.com?param=value";
        String repositoryKey = "repKey";
        createJob(url);

        FinishJobRequest finishJobRequest = new FinishJobRequest(url, repositoryKey);

        Response updateResp = makePatchCall(famousPeopleJobPath, finishJobRequest);
        assertThat(updateResp.code(), is(HttpStatus.OK.value()));

        FamousPeopleJobDto job = searchParticularJob(url);
        assertThat(job.getRepositoryKey(), is(repositoryKey));
    }

    private Response makePatchCall(String path, Object body) throws IOException {
        Request createJobRequest = new Request.Builder()
                .url(hostname + path)
                .patch(RequestBody.create(JSON, mapper.writeValueAsString(body)))
                .build();

        return client.newCall(createJobRequest).execute();
    }

    /**
     * Specification (1) c
     * Again an unknown URL is an error.
     */
    @Test
    public void testUrlNotFoundOnFinish() throws IOException {
        String url = "http://something.com?param=value";
        String repositoryKey = "repKey";
        createJob(url);

        FinishJobRequest finishJobRequest = new FinishJobRequest(url + "!", repositoryKey);

        Response updateResp = makePatchCall(famousPeopleJobPath, finishJobRequest);
        assertThat(updateResp.code(), is(HttpStatus.NOT_FOUND.value()));
    }

    /**
     * Specification (1) c
     * This is the last modification call for the job and marks the end of it.
     */
    @Test
    public void testAfterJobFinishesNoUpdatesAreAllowed() throws IOException {
        String url = "http://something.com?param=value";
        String repositoryKey = "repKey";
        createJob(url);

        FinishJobRequest finishJobRequest = new FinishJobRequest(url, repositoryKey);
        Response updateResp = makePatchCall(famousPeopleJobPath, finishJobRequest);
        assertThat(updateResp.code(), is(HttpStatus.OK.value()));

        UpdateListRequest updateListRequest = new UpdateListRequest(url);
        Response listResp = makePutCall(famousPeopleJobPath + "/list", updateListRequest);
        assertThat(listResp.code(), is(HttpStatus.UNPROCESSABLE_ENTITY.value()));
    }

    /**
     * Specification (2) a
     * Crawler might ask to retrieve any information about given URL - a current list of famous people and the repository key.
     */
    @Test
    public void testGetParticularJob() throws IOException {
        String url = "http://something.com?param=value";
        String repositoryKey = "repKey";
        createJob(url);


        UpdateListRequest updateListRequest = new UpdateListRequest(url);
        List<String> famousPeople = new ArrayList<>();
        famousPeople.add("Philip Seymour Hoffman");

        updateListRequest.setList(famousPeople);

        Response updateResp = makePutCall(famousPeopleJobPath + "/list", updateListRequest);
        assertThat(updateResp.code(), is(HttpStatus.OK.value()));

        FinishJobRequest finishJobRequest = new FinishJobRequest(url, repositoryKey);
        Response finishResp = makePatchCall(famousPeopleJobPath, finishJobRequest);
        assertThat(finishResp.code(), is(HttpStatus.OK.value()));


        FamousPeopleJobDto job = searchParticularJob(url);

        assertThat(job.getUrl(), is(url));
        assertThat(job.getRepositoryKey(), is(repositoryKey));
        assertThat(job.getFamousPeople(), contains(famousPeople.toArray()));
    }

    /**
     * Specification (2) b
     * Crawler might ask to retrieve any jobs, which are not finished at the moment.
     */
    @Test
    public void testUnfinishedJobsRetrieval() throws IOException {
        String url1 = "http://something.com?param=value";
        String url2 = "http://something.com?param=other value";
        String url3 = "http://somethingelse.com";
        String repositoryKey = "repKey";
        createJob(url1);
        createJob(url2);
        createJob(url3);


        UpdateListRequest updateListRequest = new UpdateListRequest(url1);
        List<String> famousPeople = new ArrayList<>();
        famousPeople.add("Philip Seymour Hoffman");

        updateListRequest.setList(famousPeople);

        Response updateResp = makePutCall(famousPeopleJobPath + "/list", updateListRequest);
        assertThat(updateResp.code(), is(HttpStatus.OK.value()));

        FinishJobRequest finishJobRequest = new FinishJobRequest(url1, repositoryKey);
        Response finishResp = makePatchCall(famousPeopleJobPath, finishJobRequest);
        assertThat(finishResp.code(), is(HttpStatus.OK.value()));


        List<FamousPeopleJobDto> jobs = searchUnfinishedJobs();
        assertThat(jobs.size(), is(2));
        assertThat(jobs, containsInAnyOrder(hasProperty("url", is(url2)), hasProperty("url", is(url3))
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

}
