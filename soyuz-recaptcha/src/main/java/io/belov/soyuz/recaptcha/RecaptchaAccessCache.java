package io.belov.soyuz.recaptcha;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by fbelov on 17.12.16.
 */
public class RecaptchaAccessCache {

    private Cache<Access, Boolean> cache;
    private int maxItemsPerIp;

    public RecaptchaAccessCache(long expireDurationInSeconds, int maxItemsPerIp) {
        this.cache = CacheBuilder.newBuilder().expireAfterWrite(expireDurationInSeconds, TimeUnit.SECONDS).build();
        this.maxItemsPerIp = maxItemsPerIp;
    }

    public void access(String ip) {
        cache.put(Access.from(ip), true);
    }

    public boolean isLimitExceededForIp(String ip) {
        return cache.asMap().keySet().stream().filter(a -> a.getIp().equals(ip)).count() > maxItemsPerIp;
    }

    @Getter
    @EqualsAndHashCode
    private static class Access {
        private String ip;
        private Date date;

        private Access(String ip, Date date) {
            this.ip = ip;
            this.date = date;
        }

        public static Access from(String ip) {
            return new Access(ip, new Date());
        }
    }
}
