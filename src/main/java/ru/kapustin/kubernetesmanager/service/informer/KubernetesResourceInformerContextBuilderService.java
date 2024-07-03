package ru.kapustin.kubernetesmanager.service.informer;

import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.openapi.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kapustin.kubernetesmanager.model.KubernetesResourceInformerContext;

@Service
@RequiredArgsConstructor
public class KubernetesResourceInformerContextBuilderService {

    public KubernetesResourceInformerContext buildContext(SharedInformerFactory informerFactory) {
        SharedIndexInformer<V1Pod> podInformer = informerFactory.getExistingSharedIndexInformer(V1Pod.class);
        SharedIndexInformer<V1Node> nodeInformer = informerFactory.getExistingSharedIndexInformer(V1Node.class);
        SharedIndexInformer<V1Ingress> ingressInformer = informerFactory.getExistingSharedIndexInformer(V1Ingress.class);

        return new KubernetesResourceInformerContext(podInformer, nodeInformer, ingressInformer);
    }
}
