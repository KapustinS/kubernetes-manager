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
public class ResourceEventHandlerBuilderFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceEventHandlerBuilderFacade.class);

    public Optional<ResourceEventHandler<V1Pod>> getPodResourceEventHandler(KubernetesResourceInformerContext context) {
        if (context == null) {
            return Optional.empty();
        }
        return Optional.of(new ResourceEventHandler<V1Pod>() {
            @Override
            public void onAdd(V1Pod pod) {
                if(context.podInformer().hasSynced()) {
                    LOGGER.info("Pod {} added", pod.getMetadata().getName());
                }
            }

            @Override
            public void onUpdate(V1Pod oldPod, V1Pod newPod) {
                if(context.podInformer().hasSynced()) {
                    LOGGER.info("Pod {} updated", newPod.getMetadata().getName());
                }
            }

            @Override
            public void onDelete(V1Pod pod, boolean deletedFinalStateUnknown) {
                if(context.podInformer().hasSynced()) {
                    LOGGER.info("Pod {} deleted", pod.getMetadata().getName());
                }
            }
        });
    }

    public Optional<ResourceEventHandler<V1Node>> getNodeResourceEventHandler(KubernetesResourceInformerContext context) {
        if (context == null) {
            return Optional.empty();
        }
        return Optional.of(new ResourceEventHandler<V1Node>() {
            @Override
            public void onAdd(V1Node node) {
            }

            @Override
            public void onUpdate(V1Node oldNode, V1Node newNode) {
            }

            @Override
            public void onDelete(V1Node node, boolean deletedFinalStateUnknown) {
            }
        });
    }

    public Optional<ResourceEventHandler<V1Ingress>> getIngressResourceEventHandler(KubernetesResourceInformerContext context) {
        if (context == null) {
            return Optional.empty();
        }

        return Optional.of(new ResourceEventHandler<V1Ingress>() {
            @Override
            public void onAdd(V1Ingress ingress) {
            }

            @Override
            public void onUpdate(V1Ingress oldIngress, V1Ingress newIngress) {
            }

            @Override
            public void onDelete(V1Ingress ingress, boolean deletedFinalStateUnknown) {
            }
        });
    }
}
