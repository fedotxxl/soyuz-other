package io.belov.soyuz.validator;

import io.belov.soyuz.validator.message.FvMessageResolverI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fbelov on 14.11.16.
 */
public class FvJaxrs {

    @Provider
    public static class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<FluentValidator.ValidationException> {

        private FvMessageResolverI fvMessageResolver;

        public ExceptionMapper(FvMessageResolverI fvMessageResolver) {
            this.fvMessageResolver = fvMessageResolver;
        }

        @Override
        public Response toResponse(FluentValidator.ValidationException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(exceptionToBean(ex))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        private FvRest.ErrorsResponse exceptionToBean(FluentValidator.ValidationException ex) {
            Object rootObject = ex.getRootObject();
            List<String> common = new ArrayList<>();
            Map<String, String> properties = new HashMap<>();

            for (FluentValidator.Error e : ex.getErrors()) {
                String property = e.getProperty();
                String message = fvMessageResolver.getMessage(rootObject, e);

                if (property != null) {
                    properties.put(property, message);
                } else {
                    common.add(message);
                }
            }

            return new FvRest.ErrorsResponse(common, properties);
        }
    }

}
