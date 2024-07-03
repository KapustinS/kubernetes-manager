package ru.kapustin.kubernetesmanager.service.informer;

import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.CallGenerator;
import io.kubernetes.client.util.CallGeneratorParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KubernetesResourceInformerFactoryService {
    private static final Long RESYNC_PERIOD_SECONDS = 600L;
    private static final Integer TIMEOUT = 300;

    public void registerInformers(SharedInformerFactory informerFactory, CoreV1Api coreV1Api, NetworkingV1Api networkingV1Api) {
        CallGenerator podCallGenerator = getPodCallGenerator(coreV1Api);
        CallGenerator nodeCallGenerator = getNodeCallGenerator(coreV1Api);
        CallGenerator ingressCallGenerator = getIngressCallGenerator(networkingV1Api);

        informerFactory.sharedIndexInformerFor(podCallGenerator, V1Pod.class, V1PodList.class, RESYNC_PERIOD_SECONDS);
        informerFactory.sharedIndexInformerFor(nodeCallGenerator, V1Node.class, V1NodeList.class, RESYNC_PERIOD_SECONDS);
        informerFactory.sharedIndexInformerFor(ingressCallGenerator, V1Ingress.class, V1IngressList.class, RESYNC_PERIOD_SECONDS);
    }

    private CallGenerator getPodCallGenerator(CoreV1Api coreV1Api) {
        return (CallGeneratorParams params) -> coreV1Api.listPodForAllNamespaces()
                .resourceVersion(params.resourceVersion)
                .watch(params.watch)
                .timeoutSeconds(TIMEOUT)
                .buildCall(null);
    }

    private CallGenerator getNodeCallGenerator(CoreV1Api coreV1Api) {
        return (CallGeneratorParams params) -> coreV1Api.listNode()
                .resourceVersion(params.resourceVersion)
                .watch(params.watch)
                .timeoutSeconds(TIMEOUT)
                .buildCall(null);
    }

    private CallGenerator getIngressCallGenerator(NetworkingV1Api networkingV1Api) {
        return (CallGeneratorParams params) -> networkingV1Api.listIngressForAllNamespaces()
                .resourceVersion(params.resourceVersion)
                .watch(params.watch)
                .timeoutSeconds(TIMEOUT)
                .buildCall(null);
    }
}



