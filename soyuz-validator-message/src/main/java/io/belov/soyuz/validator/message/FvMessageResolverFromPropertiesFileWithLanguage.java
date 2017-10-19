package io.belov.soyuz.validator.message;

import io.belov.soyuz.validator.FluentValidator;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 16.10.17.
 */
@AllArgsConstructor
public class FvMessageResolverFromPropertiesFileWithLanguage extends FvMessageResolverFromPropertiesFile.BasePropertyNameResolver {

    private FvMessageResolverLanguageProviderI languageSupplier;

    @Override
    public <R, V> List<String> getPropertyNames(R rootObject, FluentValidator.Error<V> error) {
        String lang = languageSupplier.getLanguage();
        List<String> propertyNames = super.getPropertyNames(rootObject, error);

        if (lang != null && !"".equals(lang)) {
            List<String> answer = new ArrayList<>();

            for (String property : propertyNames) {
                answer.add(property + "." + lang);
                answer.add(property);
            }

            return answer;
        } else {
            return propertyNames;
        }
    }
}
