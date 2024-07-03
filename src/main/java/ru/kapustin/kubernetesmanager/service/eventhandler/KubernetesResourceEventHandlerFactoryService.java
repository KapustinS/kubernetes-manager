package ru.kapustin.kubernetesmanager.service.eventhandler;

import io.kubernetes.client.informer.ResourceEventHandler;
import io.kubernetes.client.openapi.models.V1Ingress;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1Pod;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.kapustin.kubernetesmanager.model.KubernetesResourceInformerContext;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KubernetesResourceEventHandlerFactoryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesResourceEventHandlerFactoryService.class);
    private final ResourceEventHandlerBuilderFacade handlerBuilderFacade;

    public ResourceEventHandler<V1Pod> createPodResourceEventHandler(KubernetesResourceInformerContext context) {
        Optional<ResourceEventHandler<V1Pod>> resourceEventHandlerOptional = handlerBuilderFacade.getPodResourceEventHandler(context);
        if (resourceEventHandlerOptional.isEmpty()) {
            LOGGER.error("Pod ResourceEventHandler is null");
            return null;
        }
        return resourceEventHandlerOptional.get();
    }

    public ResourceEventHandler<V1Node> createNodeResourceEventHandler(KubernetesResourceInformerContext context) {
        Optional<ResourceEventHandler<V1Node>> resourceEventHandlerOptional = handlerBuilderFacade.getNodeResourceEventHandler(context);
        if (resourceEventHandlerOptional.isEmpty()) {
            LOGGER.error("Node ResourceEventHandler is null");
            return null;
        }
        return resourceEventHandlerOptional.get();
    }

    public ResourceEventHandler<V1Ingress> createIngressResourceEventHandler(KubernetesResourceInformerContext context) {
        Optional<ResourceEventHandler<V1Ingress>> resourceEventHandlerOptional = handlerBuilderFacade.getIngressResourceEventHandler(context);
        if (resourceEventHandlerOptional.isEmpty()) {
            LOGGER.error("Ingress ResourceEventHandler is null");
            return null;
        }
        return resourceEventHandlerOptional.get();
    }
}

