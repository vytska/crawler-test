package vytautas.com.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vytautas.com.entities.FamousPeopleJob;
import vytautas.com.dtos.FamousPeopleJobDto;
import vytautas.com.dtos.FinishJobRequest;
import vytautas.com.dtos.UpdateListRequest;
import vytautas.com.dtos.UrlHolder;
import vytautas.com.exceptions.JobAlreadyExistsWarning;
import vytautas.com.exceptions.JobAlreadyFinishedException;
import vytautas.com.exceptions.JobNotFoundException;
import vytautas.com.exceptions.UrlRequiredException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service(value = "jobTracker")
public class JobTracker {

    private static Logger logger = LoggerFactory.getLogger(JobTracker.class);

    private static ConcurrentHashMap<String, FamousPeopleJob> jobs = new ConcurrentHashMap<>();


    public void addJob(String url) {
        validateUrl(url);
        FamousPeopleJob existingJob = jobs.get(url);
        if (existingJob != null) {
            logger.warn("JobTracker.addJob. Job already exists for URL: {}, request rejected", url);
            throw new JobAlreadyExistsWarning();
        }

        logger.info("JobTracker.addJob. Adding job for URL: {}", url);
        FamousPeopleJob famousPeopleJob = new FamousPeopleJob(url);
        jobs.put(url, famousPeopleJob);
    }

    public void updateJob(UpdateListRequest updateListRequest) {
        FamousPeopleJob job = getJobAndValidate(updateListRequest);
        logger.warn("JobTracker.updateJob. Adding famous people for job with URL: {}", updateListRequest.getUrl());
        job.addFamousPeople(updateListRequest.getList());
    }

    private FamousPeopleJob getJobAndValidate(UrlHolder urlHolder) {
        validateUrl(urlHolder.getUrl());

        FamousPeopleJob existingJob = jobs.get(urlHolder.getUrl());

        if (existingJob == null) {
            logger.warn("JobTracker.getJobAndValidate. Job for URL: {} was not found, request rejected", urlHolder.getUrl());
            throw new JobNotFoundException();
        }

        if (existingJob.isFinished()) {
            logger.warn("JobTracker.getJobAndValidate. Job for URL: {} is already finished, request rejected", urlHolder.getUrl());
            throw new JobAlreadyFinishedException();
        }

        return existingJob;
    }

    private void validateUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            logger.warn("JobTracker.validateUrl. URL was empty, request rejected");
            throw new UrlRequiredException();
        }
    }

    public void finishJob(FinishJobRequest finishJobRequest) {
        FamousPeopleJob existingJob = getJobAndValidate(finishJobRequest);
        logger.info("JobTracker.finishJob. Finishing job for URL: {}", finishJobRequest.getUrl());
        existingJob.setRepositoryKey(finishJobRequest.getRepositoryKey());
    }

    public Set<FamousPeopleJobDto> searchByState(String state) {
        if(state.equalsIgnoreCase("unfinished")) {
            return getUnfinishedJobs();
        } else {
            logger.warn("JobTracker.searchByState. Invalid search state param {}, returning empty set", state);
            return Collections.emptySet();
        }
    }

    private Set<FamousPeopleJobDto> getUnfinishedJobs(){
        logger.info("JobTracker.getUnfinishedJobs");
        Set<FamousPeopleJobDto> unfinishedJobs = new HashSet<>();
        jobs.forEachValue(Long.MAX_VALUE, job -> {
            if (!job.isFinished()) {
                unfinishedJobs.add(job.toDto());
            }
        });

        return unfinishedJobs;
    }

    public Set<FamousPeopleJobDto> searchByUrl(String url) {
        logger.info("JobTracker.searchByUrl. URL: {}", url);
        FamousPeopleJob famousPeopleJob = jobs.get(url);
        Set<FamousPeopleJobDto> jobs = new HashSet<>();

        if(famousPeopleJob != null) {
            jobs.add(famousPeopleJob.toDto());
        }

        return jobs;
    }

    public void clearJobs() {
        logger.info("JobTracker.clearJobs");
        jobs.clear();
    }
}
