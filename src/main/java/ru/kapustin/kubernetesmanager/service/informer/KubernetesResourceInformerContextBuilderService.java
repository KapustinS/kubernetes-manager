package ru.kapustin.kubernetesmanager.service.informer;

import io.kubernetes.client.informer.ResourceEventHandler;
import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.openapi.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kapustin.kubernetesmanager.model.KubernetesResourceInformerContext;
import ru.kapustin.kubernetesmanager.service.eventhandler.KubernetesResourceEventHandlerFactoryService;

@Service
@RequiredArgsConstructor
public class KubernetesResourceInformerContextBuilderService {
    private final KubernetesResourceEventHandlerFactoryService eventHandlerFactoryService;

    public KubernetesResourceInformerContext buildContext(SharedInformerFactory informerFactory) {
        SharedIndexInformer<V1Pod> podInformer = informerFactory.getExistingSharedIndexInformer(V1Pod.class);
        SharedIndexInformer<V1Node> nodeInformer = informerFactory.getExistingSharedIndexInformer(V1Node.class);
        SharedIndexInformer<V1Ingress> ingressInformer = informerFactory.getExistingSharedIndexInformer(V1Ingress.class);

        KubernetesResourceInformerContext context = new KubernetesResourceInformerContext(podInformer, nodeInformer, ingressInformer);

        ResourceEventHandler<V1Pod> podResourceEventHandler = eventHandlerFactoryService.createPodResourceEventHandler(context);
        ResourceEventHandler<V1Node> nodeResourceEventHandler = eventHandlerFactoryService.createNodeResourceEventHandler(context);
        ResourceEventHandler<V1Ingress> ingressResourceEventHandler = eventHandlerFactoryService.createIngressResourceEventHandler(context);

        podInformer.addEventHandler(podResourceEventHandler);
        nodeInformer.addEventHandler(nodeResourceEventHandler);
        ingressInformer.addEventHandler(ingressResourceEventHandler);

        return context;
    }
}
