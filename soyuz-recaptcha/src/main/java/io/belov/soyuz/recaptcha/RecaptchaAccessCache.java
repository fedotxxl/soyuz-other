package io.belov.soyuz.recaptcha;

import com.google.common.base.Supplier;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.primitives.Booleans;
import io.thedocs.soyuz.to;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by fbelov on 17.12.16.
 */
public class RecaptchaAccessCache {

    private Cache<Access, Boolean> cacheAccess;
    private Cache<String, Boolean> cacheExceededForce;
    private int maxItemsPerIp;

    public RecaptchaAccessCache(long expireDurationInSeconds, int maxItemsPerIp) {
        this.cacheAccess = CacheBuilder.newBuilder().expireAfterWrite(expireDurationInSeconds, TimeUnit.SECONDS).build();
        this.cacheExceededForce = CacheBuilder.newBuilder().expireAfterWrite(expireDurationInSeconds, TimeUnit.SECONDS).build();
        this.maxItemsPerIp = maxItemsPerIp;
    }

    public void access(String ip) {
        cacheAccess.put(Access.from(ip), true);
    }

    public void markAsExceeded(String ip) {
        cacheExceededForce.put(ip, true);
    }

    public boolean isLimitExceededForIp(String ip) {
        Supplier<Boolean> isForced = () -> to.or(cacheExceededForce.getIfPresent(ip), false);
        Supplier<Boolean> isExceeded = () -> cacheAccess.asMap().keySet().stream().filter(a -> a.getIp().equals(ip)).count() > maxItemsPerIp;

        return isForced.get() || isExceeded.get();
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
