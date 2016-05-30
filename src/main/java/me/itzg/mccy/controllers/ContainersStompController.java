package me.itzg.mccy.controllers;

import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.exceptions.DockerRequestException;
import me.itzg.mccy.model.ContainerCreateStatus;
import me.itzg.mccy.model.ContainerRequest;
import me.itzg.mccy.services.ContainersService;
import me.itzg.mccy.types.MccyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@Controller
@MessageMapping("/containers")
public class ContainersStompController {
    public static final String TOPIC_CREATE_STATUS = "/topic/containers/create-status";
    private static Logger LOG = LoggerFactory.getLogger(ContainersStompController.class);

    @Autowired
    private ContainersService containers;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/create")
    public void createContainer(Principal principal, @Header("request") String requestId,
                                @Valid ContainerRequest request) {

        final String user = principal.getName();
        final Map<String, Object> headers = new HashMap<>();
        headers.put("request", requestId);

        try {
            containers.create(request, user, (status) ->
                    messagingTemplate.convertAndSendToUser(user, TOPIC_CREATE_STATUS, status, headers));

        } catch (MccyException | DockerException | InterruptedException e) {
            final ContainerCreateStatus status = new ContainerCreateStatus();
            status.setState(ContainerCreateStatus.State.ERROR);
            if (e instanceof DockerRequestException) {
                final DockerRequestException dockerException = (DockerRequestException) e;
                status.setDetails(dockerException.message());
            }
            else {
                status.setDetails(e.getMessage());
            }

            messagingTemplate.convertAndSendToUser(user, TOPIC_CREATE_STATUS, status, headers);

            LOG.warn("Failed while creating container", e);
        }
    }

}
