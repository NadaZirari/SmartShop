package service;

import java.util.List;
import java.util.Map;

import entity.Client;

public interface ClientService {
	Client createClient(Client client);
    Client updateClient(Long id, Client client);
    void deleteClient(Long id);
    Client getClient(Long id);
    List<Client> getAllClients();
    Map<String, Object> getClientStats(Long id);
    void updateFidelityLevel(Client client);

}
