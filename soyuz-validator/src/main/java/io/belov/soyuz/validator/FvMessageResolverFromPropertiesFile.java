package io.belov.soyuz.validator;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by fbelov on 07.06.16.
 */
public class FvMessageResolverFromPropertiesFile implements FvMessageResolverI {

    private PropertyNameResolver propertyNameResolver;
    private Properties properties;
    private boolean strictMode;

    public FvMessageResolverFromPropertiesFile(File file, PropertyNameResolver propertyNameResolver) {
        this(file, propertyNameResolver, true);
    }

    public FvMessageResolverFromPropertiesFile(File file, PropertyNameResolver propertyNameResolver, boolean strictMode) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            this.properties = new Properties();
            this.propertyNameResolver = propertyNameResolver;
            this.strictMode = strictMode;

            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FvMessageResolverFromPropertiesFile(InputStream inputStream, PropertyNameResolver propertyNameResolver) {
        this(inputStream, propertyNameResolver, true);
    }

    public FvMessageResolverFromPropertiesFile(InputStream inputStream, PropertyNameResolver propertyNameResolver, boolean strictMode) {
        try {
            this.properties = new Properties();
            this.propertyNameResolver = propertyNameResolver;
            this.strictMode = strictMode;

            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public <R, V> String getMessage(R rootObject, FluentValidator.Error<V> error) {
        List<String> propertyNames = propertyNameResolver.getPropertyNames(rootObject, error);

        for (String propertyName : propertyNames) {
            String message = properties.getProperty(propertyName);

            if (message != null) {
                return formatMessage(message, error);
            }
        }

        if (strictMode) {
            throw new IllegalStateException("Unable to get message for an error " + error);
        } else {
            return null;
        }
    }

    private <V> String formatMessage(String message, FluentValidator.Error<V> error) {
        return MessageFormat.format(message.replace("{:property}", (error.hasProperty()) ? error.getProperty() : "").replace("{:value}", (error.hasValue()) ? error.getValue().toString() : ""), error.getArgs());
    }

    public interface PropertyNameResolver {
        <R, V> List<String> getPropertyNames(R rootObject, FluentValidator.Error<V> error);
    }

    public static class BasePropertyNameResolver implements PropertyNameResolver {
        @Override
        public <R, V> List<String> getPropertyNames(R rootObject, FluentValidator.Error<V> error) {
            List<String> answer = new ArrayList<>();
            String property = error.getProperty();
            String code = error.getCode();

            if (property == null) {
                property = "common";
            }

            String propertyWithCode = property + "." + code;

            answer.add(rootObject.getClass().getCanonicalName() + "." + propertyWithCode);
            answer.add(rootObject.getClass().getSimpleName() + "." + propertyWithCode);
            answer.add(propertyWithCode);
            answer.add(code);

            return answer;
        }
    }
}
