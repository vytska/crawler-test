package vytautas.com.dto;

public class FinishJobRequest extends UrlHolder {

    public FinishJobRequest(){}

    public FinishJobRequest(String url, String repositoryKey) {
        super(url);
        this.repositoryKey = repositoryKey;
    }

    private String repositoryKey;

    public String getRepositoryKey() {
        return repositoryKey;
    }

    public void setRepositoryKey(String repositoryKey) {
        this.repositoryKey = repositoryKey;
    }
}
