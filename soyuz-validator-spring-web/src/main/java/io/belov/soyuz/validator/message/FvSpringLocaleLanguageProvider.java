package io.belov.soyuz.validator.message;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Created on 16.10.17.
 */
public class FvSpringLocaleLanguageProvider implements FvMessageResolverLanguageProviderI {

    @Override
    public String getLanguage() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes)requestAttributes).getRequest().getLocale().getLanguage();
        } else {
            return null;
        }
    }

}
