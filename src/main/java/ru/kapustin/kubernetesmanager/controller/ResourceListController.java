package ru.kapustin.kubernetesmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.kapustin.kubernetesmanager.model.IngressListResponse;
import ru.kapustin.kubernetesmanager.model.NodeListResponse;
import ru.kapustin.kubernetesmanager.model.PodListResponse;
import ru.kapustin.kubernetesmanager.service.business.IngressListService;
import ru.kapustin.kubernetesmanager.service.business.NodeListService;
import ru.kapustin.kubernetesmanager.service.business.PodListService;

@RestController
@RequiredArgsConstructor
public class ResourceListController implements ResourceListApi {
    private final PodListService podListService;
    private final NodeListService nodeListService;
    private final IngressListService ingressListService;

    @Override
    public ResponseEntity<IngressListResponse> getIngresses(String namespace) {
        return ResponseEntity.ok(ingressListService.getIngresses(namespace));
    }

    @Override
    public ResponseEntity<NodeListResponse> getNodes() {
        return ResponseEntity.ok(nodeListService.getNodes());
    }

    @Override
    public ResponseEntity<PodListResponse> getPods() {
        return ResponseEntity.ok(podListService.getPods());
    }
}
