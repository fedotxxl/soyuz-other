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
        Object[] argsWithValue;
        V value = error.getValue();
        Object[] args = error.getArgs();
        List<String> propertyNames = propertyNameResolver.getPropertyNames(rootObject, error);

        if (args != null && args.length > 0) {
            argsWithValue = new Object[args.length + 1];
            argsWithValue[0] = value;

            System.arraycopy(args, 0, argsWithValue, 1, args.length);
        } else {
            argsWithValue = new Object[]{value};
        }

        for (String propertyName : propertyNames) {
            String message = properties.getProperty(propertyName);

            if (message != null) {
                return MessageFormat.format(message, argsWithValue);
            }
        }

        if (strictMode) {
            throw new IllegalStateException("Unable to get message for an error " + error);
        } else {
            return null;
        }
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

            answer.add(property + "." + code);
            answer.add(code);

            return answer;
        }
    }
}
