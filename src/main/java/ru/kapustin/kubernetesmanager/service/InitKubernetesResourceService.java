package ru.kapustin.kubernetesmanager.service;

import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.kapustin.kubernetesmanager.model.KubernetesResourceInformerContext;
import ru.kapustin.kubernetesmanager.service.informer.KubernetesResourceInformerContextBuilderService;
import ru.kapustin.kubernetesmanager.service.informer.KubernetesResourceInformerFactoryService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InitKubernetesResourceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitKubernetesResourceService.class);

    private final KubernetesResourceService kubernetesResourceService;
    private final KubernetesResourceInformerFactoryService informerFactoryService;
    private final KubernetesResourceInformerContextBuilderService contextBuilderService;
    private final KubernetesResourceInformerContextManager contextManager;

    public void watchResources() {
        Optional<SharedInformerFactory> informerFactoryOptional = kubernetesResourceService.getSharedInformerFactory();
        if (informerFactoryOptional.isEmpty()) {
            LOGGER.error("Failed to initialize KubernetesApiFactory due to missing SharedInformerFactory.");
            return;
        }
        SharedInformerFactory informerFactory = informerFactoryOptional.get();

        Optional<CoreV1Api> coreV1ApiOptional = kubernetesResourceService.getCoreV1Api();
        if (coreV1ApiOptional.isEmpty()) {
            LOGGER.error("Failed to initialize KubernetesApiFactory due to missing CoreV1Api.");
            return;
        }
        CoreV1Api coreV1Api = coreV1ApiOptional.get();

        Optional<NetworkingV1Api> networkingV1ApiOptional = kubernetesResourceService.getNetworkingApi();
        if (networkingV1ApiOptional.isEmpty()) {
            LOGGER.error("Failed to initialize KubernetesApiFactory due to missing NetworkingV1Api.");
            return;
        }
        NetworkingV1Api networkingV1Api = networkingV1ApiOptional.get();

        informerFactoryService.registerInformers(informerFactory, coreV1Api, networkingV1Api);

        KubernetesResourceInformerContext context = contextBuilderService.buildContext(informerFactory);

        contextManager.putContext(context);

        informerFactory.startAllRegisteredInformers();
    }
}
