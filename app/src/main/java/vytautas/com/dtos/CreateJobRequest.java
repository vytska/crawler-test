package vytautas.com.dtos;

public class CreateJobRequest {

    private String url;

    public CreateJobRequest(){}

    public CreateJobRequest(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
