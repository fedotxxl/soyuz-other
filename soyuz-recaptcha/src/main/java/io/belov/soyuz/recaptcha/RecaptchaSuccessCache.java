package io.belov.soyuz.recaptcha;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.belov.soyuz.utils.is;
import io.belov.soyuz.utils.to;

import java.util.concurrent.TimeUnit;

/**
 * Created by fbelov on 17.12.16.
 */
public class RecaptchaSuccessCache {

    private Cache<String, Boolean> cache;

    public RecaptchaSuccessCache(long expireDurationInSeconds) {
        this.cache = CacheBuilder.newBuilder().expireAfterWrite(expireDurationInSeconds, TimeUnit.SECONDS).build();
    }

    public void put(String recaptcha) {
        cache.put(recaptcha, true);
    }

    public void remove(String recaptcha) {
        cache.invalidate(recaptcha);
    }

    public boolean has(String recaptcha) {
        if (is.tt(recaptcha)) {
            return to.or(cache.getIfPresent(recaptcha), false);
        } else {
            return false;
        }
    }
}