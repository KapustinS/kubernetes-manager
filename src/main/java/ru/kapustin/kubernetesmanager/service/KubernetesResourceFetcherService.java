package ru.kapustin.kubernetesmanager.service;

import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.cache.Store;
import io.kubernetes.client.openapi.models.V1Ingress;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1Pod;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.kapustin.kubernetesmanager.model.KubernetesResourceInformerContext;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KubernetesResourceFetcherService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesResourceFetcherService.class);

    private final KubernetesResourceInformerContextManager contextManager;

    public List<V1Pod> getPods() {
        KubernetesResourceInformerContext context = contextManager.getContext();
        if (context == null) {
            LOGGER.error("PodInformerContext is null");
            return List.of();
        }
        return getV1Pods(context);
    }

    public List<V1Pod> getNamespacedPods(String namespace) {
        KubernetesResourceInformerContext context = contextManager.getContext();
        if (context == null) {
            LOGGER.error("PodInformerContext is null");
            return List.of();
        }
        List<V1Pod> pods = getV1Pods(context);
        return filteredPods(namespace, pods);
    }

    public List<V1Node> getNodes() {
        KubernetesResourceInformerContext context = contextManager.getContext();
        if (context == null) {
            LOGGER.error("NodeInformerContext is null");
            return List.of();
        }
        return getV1Nodes(context);
    }

    public List<V1Ingress> getNamespacedIngresses(String namespace) {
        KubernetesResourceInformerContext context = contextManager.getContext();
        if (context == null) {
            LOGGER.error("IngressInformerContext is null");
            return List.of();
        }
        List<V1Ingress> ingresses = getV1Ingresses(context);
        return filteredIngresses(namespace, ingresses);
    }

    protected List<V1Pod> filteredPods(String namespace, List<V1Pod> pods) {
        return Optional.ofNullable(pods).orElse(List.of())
                .stream()
                .filter(pod -> pod.getMetadata() != null)
                .filter(pod -> pod.getMetadata().getNamespace() != null)
                .filter(pod -> pod.getMetadata().getNamespace().equalsIgnoreCase(namespace))
                .toList();
    }

    protected List<V1Ingress> filteredIngresses(String namespace, List<V1Ingress> ingresses) {
        return Optional.ofNullable(ingresses).orElse(List.of())
                .stream()
                .filter(ingress -> ingress.getMetadata() != null)
                .filter(ingress -> ingress.getMetadata().getNamespace() != null)
                .filter(ingress -> ingress.getMetadata().getNamespace().equals(namespace))
                .toList();
    }

    protected List<V1Pod> getV1Pods(KubernetesResourceInformerContext context) {
        return Optional.ofNullable(context).map(KubernetesResourceInformerContext::podInformer).map(SharedIndexInformer::getIndexer).map(Store::list).orElse(List.of());
    }

    protected List<V1Node> getV1Nodes(KubernetesResourceInformerContext context) {
        return Optional.ofNullable(context).map(KubernetesResourceInformerContext::nodeInformer).map(SharedIndexInformer::getIndexer).map(Store::list).orElse(List.of());
    }

    protected List<V1Ingress> getV1Ingresses(KubernetesResourceInformerContext context) {
        return Optional.ofNullable(context).map(KubernetesResourceInformerContext::ingressInformer).map(SharedIndexInformer::getIndexer).map(Store::list).orElse(List.of());
    }
}
