package ru.kapustin.kubernetesmanager.model;

import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.openapi.models.V1Ingress;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1Pod;

public record KubernetesResourceInformerContext(
        SharedIndexInformer<V1Pod> podInformer,
        SharedIndexInformer<V1Node> nodeInformer,
        SharedIndexInformer<V1Ingress> ingressInformer
){
}
