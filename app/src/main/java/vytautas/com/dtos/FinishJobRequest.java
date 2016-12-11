package vytautas.com.dtos;

public class FinishJobRequest extends UrlHolder {

    private String repositoryKey;

    public FinishJobRequest(){}

    public FinishJobRequest(String url, String repositoryKey) {
        super(url);
        this.repositoryKey = repositoryKey;
    }

    public String getRepositoryKey() {
        return repositoryKey;
    }

    public void setRepositoryKey(String repositoryKey) {
        this.repositoryKey = repositoryKey;
    }
}
