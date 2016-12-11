package vytautas.com.entities;

import vytautas.com.dtos.FamousPeopleJobDto;

import java.util.ArrayList;
import java.util.List;

public class FamousPeopleJob {

    private final String url;

    private List<String> famousPeople = new ArrayList<>();

    private String repositoryKey;

    public void addFamousPeople(List<String> newPeople) {
        famousPeople.addAll(newPeople);
    }

    public FamousPeopleJob(String url) {
        this.url = url;
    }

    public boolean isFinished() {
        return repositoryKey != null;
    }

    public FamousPeopleJobDto toDto() {
        FamousPeopleJobDto dto = new FamousPeopleJobDto();
        dto.setUrl(this.url);
        dto.setRepositoryKey(this.repositoryKey);
        dto.setFamousPeople(new ArrayList<>(this.famousPeople));
        return dto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FamousPeopleJob that = (FamousPeopleJob) o;

        return url != null ? url.equals(that.url) : that.url == null;

    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getFamousPeople() {
        return famousPeople;
    }

    public String getRepositoryKey() {
        return repositoryKey;
    }

    public void setRepositoryKey(String repositoryKey) {
        this.repositoryKey = repositoryKey;
    }
}
