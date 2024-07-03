package ru.kapustin.kubernetesmanager.service;

import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeStatus;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kapustin.kubernetesmanager.mapper.ResourcesMapper;
import ru.kapustin.kubernetesmanager.model.Node;
import ru.kapustin.kubernetesmanager.model.NodeListResponse;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NodeListService {
    private final ResourcesMapper mapper;
    private final KubernetesResourceFetcherService kubernetesResourceFetcherService;
    public NodeListResponse getNodes() {
        List<V1Node> v1Nodes = kubernetesResourceFetcherService.getNodes();
        List<Node> nodes = getNodeItems(v1Nodes);
        Integer total = nodes.size();
        return getResponse(nodes, total);
    }

    private NodeListResponse getResponse(List<Node> nodes, Integer total) {
        return new NodeListResponse().nodes(nodes).total(total);
    }

    private List<Node> getNodeItems(List<V1Node> v1Nodes) {
        return v1Nodes.stream()
                .map(this::mapNode)
                .filter(Objects::nonNull)
                .toList();
    }

    private Node mapNode(V1Node v1Node) {
        String name = getName(v1Node).orElse(null);
        String status = getStatus(v1Node).orElse(null);
        Map<String, String> labels = getLabels(v1Node);
        Map<String, String> annotations = getAnnotations(v1Node);
        return mapper.mapNode(name, status, labels, annotations);
    }

    private Map<String, String> getAnnotations(V1Node v1Node) {
        return Optional.ofNullable(v1Node)
                .map(V1Node::getMetadata)
                .map(V1ObjectMeta::getAnnotations)
                .orElse(Map.of());
    }

    private Map<String, String> getLabels(V1Node v1Node) {
        return Optional.ofNullable(v1Node)
                .map(V1Node::getMetadata)
                .map(V1ObjectMeta::getLabels)
                .orElse(Map.of());
    }

    private Optional<String> getStatus(V1Node v1Node) {
        return Optional.ofNullable(v1Node)
                .map(V1Node::getStatus)
                .map(V1NodeStatus::getPhase);
    }

    private Optional<String> getName(V1Node v1Node) {
        return Optional.ofNullable(v1Node)
                .map(V1Node::getMetadata)
                .map(V1ObjectMeta::getName);
    }
}
