package me.itzg.mccy.controllers;

import com.spotify.docker.client.exceptions.DockerException;
import me.itzg.mccy.model.ContainerDetails;
import me.itzg.mccy.model.ContainerRequest;
import me.itzg.mccy.model.ContainerSummary;
import me.itzg.mccy.model.ServerStatus;
import me.itzg.mccy.model.SingleValue;
import me.itzg.mccy.services.ContainersService;
import me.itzg.mccy.types.MccyException;
import me.itzg.mccy.types.MccyUnexpectedServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * @author Geoff Bourne
 * @since 12/21/2015
 */
@RestController
@RequestMapping("/api/containers")
public class ApiContainersController {

    @Autowired
    private ContainersService containers;

    @RequestMapping(method = RequestMethod.GET)
    public List<ContainerSummary> getAllMccyContainers(Authentication auth,
                                                       UriComponentsBuilder uriComponentsBuilder)
            throws DockerException, InterruptedException {

        containers.setProxyUriBuilder(uriComponentsBuilder);

        return containers.getAll(getAuthUsername(auth));
    }

    @RequestMapping(value = "/_public", method = RequestMethod.GET)
    public List<ContainerSummary> getAllPublicMccyContainers() throws DockerException, InterruptedException {
        return containers.getAllPublic();
    }

    @RequestMapping(value = "/{containerId}", method = RequestMethod.GET)
    public ContainerDetails getContainer(@PathVariable("containerId") String containerId,
                                         Authentication auth) throws DockerException, InterruptedException {
        return containers.get(containerId, getAuthUsername(auth));
    }

    @RequestMapping(value = "/_public/{containerId}", method = RequestMethod.GET)
    public ContainerDetails getPublicContainer(@PathVariable("containerId") String containerId) throws DockerException, InterruptedException {
        return containers.get(containerId, null);
    }

    @RequestMapping(value = "/{containerId}/_status", method = RequestMethod.GET)
    public ServerStatus getContainerStatus(@PathVariable("containerId") String containerId,
                                           Authentication auth) throws DockerException, InterruptedException, TimeoutException, MccyUnexpectedServerException {
        return containers.getContainerStatus(containerId, getAuthUsername(auth));
    }

    @RequestMapping(value = "/_public/{containerId}/_status", method = RequestMethod.GET)
    public ServerStatus getPublicContainerStatus(@PathVariable("containerId") String containerId) throws DockerException, InterruptedException, TimeoutException, MccyUnexpectedServerException {
        return containers.getContainerStatus(containerId, null);
    }

    @RequestMapping(value = "/{containerId}", method = RequestMethod.DELETE)
    public void deleteContainer(@PathVariable("containerId") String containerId) throws DockerException, InterruptedException {
        containers.delete(containerId);
    }

    @RequestMapping(value = "/{containerId}/_start", method = RequestMethod.POST)
    public void startContainer(@PathVariable("containerId") String containerId) throws DockerException, InterruptedException {
        containers.start(containerId);
    }

    @RequestMapping(value = "/{containerId}/_stop", method = RequestMethod.POST)
    public void stopContainer(@PathVariable("containerId") String containerId) throws DockerException, InterruptedException {
        containers.stop(containerId);
    }

    private static String getAuthUsername(Authentication auth) {
        if (auth == null) {
            return null;
        }
        final User user = (User) auth.getPrincipal();
        return user.getUsername();
    }
}
