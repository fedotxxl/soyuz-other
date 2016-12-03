package io.belov.soyuz.text.md;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * Created by fbelov on 04.11.15.
 */
public class MdPegDownProcessorFactory extends BasePooledObjectFactory<MdPegDownProcessor> {

    @Override
    public MdPegDownProcessor create() throws Exception {
        return new MdPegDownProcessor();
    }

    @Override
    public PooledObject<MdPegDownProcessor> wrap(MdPegDownProcessor obj) {
        return new DefaultPooledObject<>(obj);
    }

}
