package com.healthx.configs;

import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

import com.healthx.db.ClientRepository;

import lombok.extern.java.Log;

@Log
@Service
public class JpaClientDetailsService implements ClientDetailsService {

    private final ClientRepository clientRepository;

    public JpaClientDetailsService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        log.info("loadClientByClientId invoked with clientId: "+clientId);
        var clientDetails = clientRepository.findClientByName(clientId)
                .map(ClientDetailsWrapper::new)
                .orElseThrow(() -> new ClientRegistrationException("Client not found"));
        log.info("ClientDetails with secret - "+clientDetails.getClientSecret());
        return clientDetails;
    }
}
