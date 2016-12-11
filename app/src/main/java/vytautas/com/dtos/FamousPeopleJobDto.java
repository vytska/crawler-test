package vytautas.com.dtos;


import java.util.ArrayList;
import java.util.List;

public class FamousPeopleJobDto {

    private String url;

    private String repositoryKey;

    private List<String> famousPeople = new ArrayList<>();

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRepositoryKey() {
        return repositoryKey;
    }

    public void setRepositoryKey(String repositoryKey) {
        this.repositoryKey = repositoryKey;
    }

    public List<String> getFamousPeople() {
        return famousPeople;
    }

    public void setFamousPeople(List<String> famousPeople) {
        this.famousPeople = famousPeople;
    }
}
