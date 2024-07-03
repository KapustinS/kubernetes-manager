package ru.kapustin.kubernetesmanager.service;

import io.kubernetes.client.informer.ResourceEventHandler;
import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.CallGenerator;
import io.kubernetes.client.util.CallGeneratorParams;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.kapustin.kubernetesmanager.model.KubernetesResourceInformerContext;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KubernetesResourceInformerContextBuilderService {

    private static final Long RESYNC_PERIOD_SECONDS = 600L;
    private static final Integer TIMEOUT = 300;
    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesResourceInformerContextBuilderService.class);
    private final ResourceEventHandlerBuilderFacade handlerBuilderFacade;


    public KubernetesResourceInformerContext getKubernetesResourceInformerContext(CoreV1Api coreV1Api, NetworkingV1Api networkingV1Api, SharedInformerFactory informerFactory) {
        SharedIndexInformer<V1Pod> podInformer = getPodInformer(informerFactory, coreV1Api);
        SharedIndexInformer<V1Node> nodeInformer = getNodeInformer(informerFactory, coreV1Api);
        SharedIndexInformer<V1Ingress> ingressInformer = getIngressInformer(informerFactory, networkingV1Api);

        KubernetesResourceInformerContext context = new KubernetesResourceInformerContext(podInformer, nodeInformer, ingressInformer);

        ResourceEventHandler<V1Pod> podResourceEventHandler = getPodResourceEventHandler(context);
        ResourceEventHandler<V1Node> nodeResourceEventHandler = getNodeResourceEventHandler(context);
        ResourceEventHandler<V1Ingress> ingressResourceEventHandler = getIngressResourceEventHandler(context);

        podInformer.addEventHandler(podResourceEventHandler);
        nodeInformer.addEventHandler(nodeResourceEventHandler);
        ingressInformer.addEventHandler(ingressResourceEventHandler);
        return context;
    }

    protected ResourceEventHandler<V1Pod> getPodResourceEventHandler(KubernetesResourceInformerContext context) {
        Optional<ResourceEventHandler<V1Pod>> resourceEventHandlerOptional = handlerBuilderFacade.getPodResourceEventHandler(context);
        if (resourceEventHandlerOptional.isEmpty()) {
            LOGGER.error("Pod ResourceEventHandler is null for instanceId: {}");
            return null;
        }
        return resourceEventHandlerOptional.get();
    }

    protected ResourceEventHandler<V1Node> getNodeResourceEventHandler(KubernetesResourceInformerContext context) {
        Optional<ResourceEventHandler<V1Node>> resourceEventHandlerOptional = handlerBuilderFacade.getNodeResourceEventHandler(context);
        if (resourceEventHandlerOptional.isEmpty()) {
            LOGGER.error("Node ResourceEventHandler is null for instanceId: {}");
            return null;
        }
        return resourceEventHandlerOptional.get();
    }

    protected ResourceEventHandler<V1Ingress> getIngressResourceEventHandler(KubernetesResourceInformerContext context) {
        Optional<ResourceEventHandler<V1Ingress>> resourceEventHandlerOptional = handlerBuilderFacade.getIngressResourceEventHandler(context);
        if (resourceEventHandlerOptional.isEmpty()) {
            LOGGER.error("Ingress ResourceEventHandler is null for instanceId: {}");
            return null;
        }
        return resourceEventHandlerOptional.get();
    }

    protected SharedIndexInformer<V1Pod> getPodInformer(SharedInformerFactory informerFactory, CoreV1Api coreV1Api) {
        CallGenerator podCallGenerator = getPodCallGenerator(coreV1Api);
        return informerFactory.sharedIndexInformerFor(podCallGenerator, V1Pod.class, V1PodList.class, RESYNC_PERIOD_SECONDS);
    }

    protected SharedIndexInformer<V1Node> getNodeInformer(SharedInformerFactory informerFactory, CoreV1Api coreV1Api) {
        CallGenerator nodeCallGenerator = getNodeCallGenerator(coreV1Api);
        return informerFactory.sharedIndexInformerFor(nodeCallGenerator, V1Node.class, V1NodeList.class);
    }

    protected SharedIndexInformer<V1Ingress> getIngressInformer(SharedInformerFactory informerFactory, NetworkingV1Api networkingV1Api) {
        CallGenerator ingressCallGenerator = getIngressCallGenerator(networkingV1Api);
        return informerFactory.sharedIndexInformerFor(ingressCallGenerator, V1Ingress.class, V1IngressList.class);
    }

    protected CallGenerator getPodCallGenerator(CoreV1Api coreV1Api) {
        return (CallGeneratorParams params) -> coreV1Api.listPodForAllNamespaces()
                .resourceVersion(params.resourceVersion)
                .watch(params.watch)
                .timeoutSeconds(TIMEOUT)
                .buildCall(null);
    }


    protected CallGenerator getNodeCallGenerator(CoreV1Api coreV1Api) {
        return (CallGeneratorParams params) -> coreV1Api.listNode()
                .resourceVersion(params.resourceVersion)
                .watch(params.watch)
                .timeoutSeconds(TIMEOUT)
                .buildCall(null);
    }
    protected CallGenerator getIngressCallGenerator(NetworkingV1Api networkingV1Api) {
        return (CallGeneratorParams params) -> networkingV1Api.listIngressForAllNamespaces()
                .resourceVersion(params.resourceVersion)
                .watch(params.watch)
                .timeoutSeconds(TIMEOUT)
                .buildCall(null);
    }
}
