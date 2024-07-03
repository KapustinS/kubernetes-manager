package ru.kapustin.kubernetesmanager.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import ru.kapustin.kubernetesmanager.model.Ingress;
import ru.kapustin.kubernetesmanager.model.Node;
import ru.kapustin.kubernetesmanager.model.Pod;

import java.time.OffsetDateTime;
import java.util.Map;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface ResourcesMapper {

    @Mapping(target = "name", source = "name")
    @Mapping(target = "namespace", source = "namespace")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "restartCount", source = "restartCount")
    @Mapping(target = "creationTimestamp", source = "creationTimestamp")
    @Mapping(target = "labels", source = "labels")
    @Mapping(target = "annotations", source = "annotations")
    Pod mapPod(String name,
               String namespace,
               String status,
               Integer restartCount,
               OffsetDateTime creationTimestamp,
               Map<String, String> labels,
               Map<String, String> annotations);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "labels", source = "labels")
    @Mapping(target = "annotations", source = "annotations")
    Node mapNode(String name, String status, Map<String, String> labels, Map<String, String> annotations);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "namespace", source = "namespace")
    @Mapping(target = "host", source = "host")
    @Mapping(target = "path", source = "path")
    Ingress mapIngress(String name, String namespace, String host, String path);
}
