package ru.kapustin.kubernetesmanager.service;

import io.kubernetes.client.openapi.models.V1ContainerStatus;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kapustin.kubernetesmanager.mapper.ResourcesMapper;
import ru.kapustin.kubernetesmanager.model.Pod;
import ru.kapustin.kubernetesmanager.model.PodListResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PodListService {
    private final ResourcesMapper mapper;
    private final KubernetesResourceFetcherService kubernetesResourceFetcherService;

    public PodListResponse getPods() {
        List<V1Pod> v1Pods = kubernetesResourceFetcherService.getPods();
        List<Pod> pods = getPodItems(v1Pods);
        Integer total = pods.size();
        return getResponse(pods, total);
    }

    private PodListResponse getResponse(List<Pod> pods, Integer total) {
        return new PodListResponse().pods(pods).total(total);
    }

    private List<Pod> getPodItems(List<V1Pod> v1Pods) {
        return v1Pods.stream()
                .map(this::mapPod)
                .filter(Objects::nonNull)
                .toList();
    }

    private Pod mapPod(V1Pod v1Pod) {
        String name = getName(v1Pod).orElse(null);
        String namespace = getNamespace(v1Pod).orElse(null);
        String status = getStatus(v1Pod).orElse(null);
        Integer restartCount = getRestartCount(v1Pod).orElse(0);
        OffsetDateTime creationTimestamp = geCreationTimestamp(v1Pod).orElse(null);
        Map<String, String> labels = getLabels(v1Pod);
        Map<String, String> annotations = getAnnotations(v1Pod);
        return mapper.mapPod(name, namespace, status, restartCount, creationTimestamp, labels, annotations);
    }

    protected Map<String, String> getAnnotations(V1Pod v1Pod) {
        return Optional.ofNullable(v1Pod)
                .map(V1Pod::getMetadata)
                .map(V1ObjectMeta::getAnnotations)
                .orElse(Map.of());
    }

    protected Map<String, String> getLabels(V1Pod v1Pod) {
        return Optional.ofNullable(v1Pod)
                .map(V1Pod::getMetadata)
                .map(V1ObjectMeta::getLabels)
                .orElse(Map.of());
    }

    protected Optional<String> getStatus(V1Pod v1Pod) {
        return Optional.ofNullable(v1Pod)
                .map(V1Pod::getStatus)
                .map(V1PodStatus::getPhase);
    }

    protected Optional<String> getNamespace(V1Pod v1Pod) {
        return Optional.ofNullable(v1Pod)
                .map(V1Pod::getMetadata)
                .map(V1ObjectMeta::getNamespace);
    }

    protected Optional<String> getName(V1Pod v1Pod) {
        return Optional.ofNullable(v1Pod)
                .map(V1Pod::getMetadata)
                .map(V1ObjectMeta::getName);
    }

    protected Optional<OffsetDateTime> geCreationTimestamp(V1Pod v1Pod) {
        return Optional.ofNullable(v1Pod)
                .map(V1Pod::getMetadata)
                .map(V1ObjectMeta::getCreationTimestamp);
    }

    protected Optional<Integer> getRestartCount(V1Pod pod) {
        return Optional.ofNullable(pod)
                .map(V1Pod::getStatus)
                .map(V1PodStatus::getContainerStatuses)
                .filter(statuses -> !statuses.isEmpty())
                .map(statuses -> statuses.get(0))
                .map(V1ContainerStatus::getRestartCount);
    }
}
