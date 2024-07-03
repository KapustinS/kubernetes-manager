package ru.kapustin.kubernetesmanager.service;

import io.kubernetes.client.informer.ResourceEventHandler;
import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.openapi.models.V1Ingress;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1Pod;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceEventHandlerBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceEventHandlerBuilder.class);

    public ResourceEventHandler<V1Pod> getPodResourceEventHandler(SharedIndexInformer<V1Pod> podInformer) {
        return new ResourceEventHandler<V1Pod>() {
            @Override
            public void onAdd(V1Pod pod) {
                if(podInformer.hasSynced()){
                    LOGGER.info("Pod {} added", pod.getMetadata().getName());
                }
            }

            @Override
            public void onUpdate(V1Pod oldPod, V1Pod newPod) {
                if(podInformer.hasSynced()){
                    LOGGER.info("Pod {} updated", newPod.getMetadata().getName());
                }
            }

            @Override
            public void onDelete(V1Pod pod, boolean deletedFinalStateUnknown) {
                if(podInformer.hasSynced()){
                    LOGGER.info("Pod {} deleted", pod.getMetadata().getName());
                }
            }
        };
    }
}
