package vytautas.com.dtos;

public class FinishJobRequest {

    private String repositoryKey;

    public FinishJobRequest(){}

    public FinishJobRequest(String repositoryKey) {
        this.repositoryKey = repositoryKey;
    }

    public String getRepositoryKey() {
        return repositoryKey;
    }

    public void setRepositoryKey(String repositoryKey) {
        this.repositoryKey = repositoryKey;
    }
}
