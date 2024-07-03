package ru.kapustin.kubernetesmanager.service;

import io.kubernetes.client.Metrics;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class KubernetesResourceService {

    @Value("${kubernetes.config-file.path}")
    private String configFilePath;

    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesResourceService.class);

    public Optional<CoreV1Api> getCoreV1Api() {
        Optional<ApiClient> clientOptional = getApiClient();
        if (clientOptional.isEmpty()) {
            return Optional.empty();
        }
        ApiClient client = clientOptional.get();
        CoreV1Api api = new CoreV1Api(client);
        return Optional.of(api);
    }

    protected Optional<String> configFile() {
        try {
            Path filePath = Paths.get(configFilePath);
            byte[] fileBytes = Files.readAllBytes(filePath);
            String configFile = new String(fileBytes);
            return Optional.of(configFile);
        } catch (IOException e) {
            LOGGER.error("Error while getting Kubernetes configFile: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<NetworkingV1Api> getNetworkingApi() {
        Optional<ApiClient> clientOptional = getApiClient();
        if (clientOptional.isEmpty()) {
            return Optional.empty();
        }
        ApiClient client = clientOptional.get();
        NetworkingV1Api api = new NetworkingV1Api();
        api.setApiClient(client);
        return Optional.of(api);
    }

    protected Optional<ApiClient> getApiClient() {
        Optional<String> configFileOptional = configFile();
        if (configFileOptional.isEmpty()) {
            LOGGER.error("Config file is empty or null.");
            return Optional.empty();
        }
        String configFile = configFileOptional.get();

        try (Reader reader = new StringReader(configFile)){
            KubeConfig kubeConfig = KubeConfig.loadKubeConfig(reader);
            ApiClient client = ClientBuilder.kubeconfig(kubeConfig).build();
            return Optional.ofNullable(client);
        } catch (IOException e) {
            LOGGER.error("Error while getting kubernetes client from config file");
            return Optional.empty();
        }
    }

    public Optional<SharedInformerFactory> getSharedInformerFactory() {
        Optional<ApiClient> clientOptional = getApiClient();
        if (clientOptional.isEmpty()) {
            LOGGER.warn("ApiClient is null.");
            return Optional.empty();
        }
        ApiClient client = clientOptional.get();
        client.setReadTimeout(0);
        SharedInformerFactory factory = new SharedInformerFactory(client);
        return Optional.of(factory);
    }
}
