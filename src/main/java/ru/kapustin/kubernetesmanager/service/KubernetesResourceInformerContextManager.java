package ru.kapustin.kubernetesmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kapustin.kubernetesmanager.model.KubernetesResourceInformerContext;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class KubernetesResourceInformerContextManager {
    private final AtomicReference<KubernetesResourceInformerContext> informerContextRef = new AtomicReference<>();

    public KubernetesResourceInformerContext getContext() {
        return this.informerContextRef.get();
    }

    public void putContext(KubernetesResourceInformerContext context) {
        this.informerContextRef.set(context);
    }
}
