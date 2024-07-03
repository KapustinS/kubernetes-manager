package ru.kapustin.kubernetesmanager.service.listener;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.kapustin.kubernetesmanager.service.InitKubernetesResourceService;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AppStartUpEventListener {
    public static final Logger LOGGER = LoggerFactory.getLogger(AppStartUpEventListener.class);

    private final InitKubernetesResourceService kubernetesResourceService;

    @EventListener(ApplicationReadyEvent.class)
    public void applicationReady() {
        LOGGER.info("Start [{}]", ApplicationReadyEvent.class.getName());
        CompletableFuture
                .runAsync(() -> {})
                .thenRunAsync(() -> {
                    try {
                        kubernetesResourceService.watchResources();
                    } catch (Exception e) {
                        LOGGER.error("ERROR on application start up event", e);
                    }
                });
        LOGGER.info("Stop [{}]", ApplicationReadyEvent.class.getName());
    }
}
