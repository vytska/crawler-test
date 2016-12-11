package vytautas.com.controllers;

import eu.hinsch.spring.boot.actuator.metric.ExecutionMetric;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import vytautas.com.dtos.CreateJobRequest;
import vytautas.com.dtos.FamousPeopleJobDto;
import vytautas.com.dtos.FinishJobRequest;
import vytautas.com.dtos.UpdateListRequest;
import vytautas.com.exceptions.SearchParamsRequiredException;
import vytautas.com.services.JobTracker;

import java.util.Set;



@RestController
@Api(description = "Famous people job management")
public class FamousPeopleJobController {


    @Autowired
    private JobTracker jobTracker;


    @ApiOperation(
            nickname = "crateJob",
            value = "Creates new famous people job",
            notes = "This call is used to start a job. URL is used as ID, <b>it must be encoded<b/>.<br/>" +
                    "Only one job per URL is permitted.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Job successfully added"),
            @ApiResponse(code = 208, message = "Job with this URL is already started"),
            @ApiResponse(code = 422, message = "Job with this URL is already done"),
            @ApiResponse(code = 400, message = "URL was not present in the request")})
    @RequestMapping(path = "/famous-people-jobs", method = RequestMethod.POST)
    @ExecutionMetric(value = "famous-people-jobs-create", loglevel = LogLevel.INFO)
    public void crateJob(@RequestBody CreateJobRequest createJob) {
        jobTracker.addJob(createJob.getUrl());
    }

    @ApiOperation(
            nickname = "updateJobList",
            value = "Appends to job famous people list",
            notes = "Multiple calls to this appends additional people to the existing list. Empty list is acceptable")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Job successfully updated"),
            @ApiResponse(code = 404, message = "Job with this URL was not found"),
            @ApiResponse(code = 422, message = "Job with this URL is already done"),
            @ApiResponse(code = 400, message = "URL was not present in the request"),
            @ApiResponse(code = 400, message = "Famous people job list is required")})
    @RequestMapping(path = "/famous-people-jobs/{url}/append", method = RequestMethod.PATCH)
    @ExecutionMetric(value = "famous-people-jobs-update", loglevel = LogLevel.INFO)
    public void updateJobList(@PathVariable("url") String url, @RequestBody UpdateListRequest updateList) {
        jobTracker.updateJob(url, updateList.getList());
    }

    @ApiOperation(
            nickname = "finishJob",
            value = "Finishes job",
            notes = "After job is finished no update calls can be made.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Job successfully finished"),
            @ApiResponse(code = 404, message = "Job with this URL was not found"),
            @ApiResponse(code = 422, message = "Job with this URL is already done"),
            @ApiResponse(code = 400, message = "URL was not present in the request") })
    @RequestMapping(path = "/famous-people-jobs/{url}/finish", method = RequestMethod.PATCH)
    @ExecutionMetric(value="famous-people-jobs-finish", loglevel = LogLevel.INFO)
    public void finishJob(@PathVariable("url") String url, @RequestBody FinishJobRequest finishJobRequestReq) {
        jobTracker.finishJob(url, finishJobRequestReq.getRepositoryKey());
    }

    @ApiOperation(
            nickname = "searchJobs",
            value = "Searches for jobs",
            notes = "Searches for particular job if 'url' query param is specified, or for unfinished jobs if 'state=unfinished' param if present.<br/>" +
                    "<b>If one of these parameters is not present error will be returned!</b>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request successful", response = FamousPeopleJobDto[].class),
            @ApiResponse(code = 400, message = "URL was not present in the request") })
    @RequestMapping(path = "/famous-people-jobs", method = RequestMethod.GET, produces = "application/json")
    @ExecutionMetric(value = "famous-people-jobs-search", loglevel = LogLevel.INFO)
    public Set<FamousPeopleJobDto> searchJobs(@RequestParam(value = "url", required = false) String url, @RequestParam(value = "state", required = false) String state) {
        if (!StringUtils.isEmpty(url)) {
            return jobTracker.searchByUrl(url);
        } else if (!StringUtils.isEmpty(state)) {
            return jobTracker.searchByState(state);
        } else {
            throw new SearchParamsRequiredException();
        }
    }


}