package io.belov.soyuz.text.md;

import com.google.common.base.Throwables;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Created by fbelov on 04.11.15.
 */
public class MdPegDownProvider {

    private ObjectPool<MdPegDownProcessor> pool;

    public MdPegDownProvider(MdPegDownProcessorFactory factory, int maxTotal) {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(maxTotal);

        pool = new GenericObjectPool<>(factory, config);
    }

    MdPegDownProcessor provide() {
        try {
            return pool.borrowObject();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    void release(MdPegDownProcessor processor) {
        try {
            pool.returnObject(processor);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

}
