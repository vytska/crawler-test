package vytautas.com.dto;


import java.util.ArrayList;
import java.util.List;

public class FamousPeopleJobDto extends UrlHolder {

    private String repositoryKey;

    private List<String> famousPeople = new ArrayList<>();

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
