package io.belov.soyuz.validator;

import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * Created by fbelov on 03.10.16.
 */
public class FvMessageResolverFromPropertiesResource extends FvMessageResolverFromPropertiesFile {

    public FvMessageResolverFromPropertiesResource(Resource resource, PropertyNameResolver propertyNameResolver) throws IOException {
        super(resource.getInputStream(), propertyNameResolver);
    }

}
