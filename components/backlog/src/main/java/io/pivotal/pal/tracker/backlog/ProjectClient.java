package io.pivotal.pal.tracker.backlog;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.web.client.RestOperations;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ProjectClient {

    private final RestOperations restOperations;
    private final String endpoint;
    Map<Long,ProjectInfo> projects = new ConcurrentHashMap<Long,ProjectInfo>();

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations = restOperations;
        this.endpoint = registrationServerEndpoint;
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo project = restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);

        projects.put(projectId, project);

        return project;
    }

    public ProjectInfo getProjectFromCache(long projectId) {
        System.out.println("Getting project with id from cache "+ projectId);
        return projects.get(projectId);
    }
}
