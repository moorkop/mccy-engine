package me.itzg.mccy.services;

import me.itzg.mccy.model.ServerStatus;
import me.itzg.mccy.types.MccyUnexpectedServerException;

import java.util.concurrent.TimeoutException;

/**
 * @author Geoff Bourne
 * @since 2/1/2016
 */
public interface ServerStatusService {
    ServerStatus queryStatus(String host, int port) throws TimeoutException, MccyUnexpectedServerException;
}
