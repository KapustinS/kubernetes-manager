package ru.kapustin.kubernetesmanager.service.business;

import io.kubernetes.client.openapi.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kapustin.kubernetesmanager.mapper.ResourcesMapper;
import ru.kapustin.kubernetesmanager.model.Ingress;
import ru.kapustin.kubernetesmanager.model.IngressListResponse;
import ru.kapustin.kubernetesmanager.service.KubernetesObjectsFetcherService;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class IngressListService {
    private final ResourcesMapper mapper;
    private final KubernetesObjectsFetcherService kubernetesObjectsFetcherService;

    public IngressListResponse getIngresses(String namespace) {
        List<V1Ingress> v1Ingresses = kubernetesObjectsFetcherService.getNamespacedIngresses(namespace);
        List<Ingress> ingresses = getIngressItems(v1Ingresses);
        Integer total = ingresses.size();
        return getResponse(ingresses, total);
    }

    private IngressListResponse getResponse(List<Ingress> ingresses, Integer total) {
        return new IngressListResponse().ingresses(ingresses).total(total);
    }

    private List<Ingress> getIngressItems(List<V1Ingress> v1Ingresses) {
        return v1Ingresses.stream()
                .map(this::mapIngress)
                .filter(Objects::nonNull)
                .toList();
    }

    private Ingress mapIngress(V1Ingress v1Ingress) {
        String name = getName(v1Ingress).orElse(null);
        String namespace = getNamespace(v1Ingress).orElse(null);
        String host = getHost(v1Ingress).orElse(null);
        String path = getPath(v1Ingress).orElse(null);
        return mapper.mapIngress(name, namespace, host, path);
    }

    private Optional<String> getPath(V1Ingress v1Ingress) {
        return Optional.ofNullable(v1Ingress)
                .map(V1Ingress::getSpec)
                .map(V1IngressSpec::getRules)
                .stream()
                .flatMap(Collection::stream)
                .flatMap(rule -> Optional.ofNullable(rule.getHttp())
                        .map(http -> http.getPaths().stream())
                        .orElseGet(Stream::empty))
                .map(V1HTTPIngressPath::getPath)
                .findFirst();
    }

    private Optional<String> getHost(V1Ingress v1Ingress) {
        return Optional.ofNullable(v1Ingress)
                .map(V1Ingress::getSpec)
                .map(V1IngressSpec::getRules)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .map(V1IngressRule::getHost)
                .findFirst();
    }

    private Optional<String> getNamespace(V1Ingress v1Ingress) {
        return Optional.ofNullable(v1Ingress)
                .map(V1Ingress::getMetadata)
                .map(V1ObjectMeta::getNamespace);
    }

    private Optional<String> getName(V1Ingress v1Ingress) {
        return Optional.ofNullable(v1Ingress)
                .map(V1Ingress::getMetadata)
                .map(V1ObjectMeta::getName);
    }
}
