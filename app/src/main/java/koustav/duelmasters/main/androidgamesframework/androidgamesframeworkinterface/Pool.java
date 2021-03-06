package koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Koustav on 2/8/2015.
 * Abstract: The Pool Class: Because Reuse is Good for You!
 *           We are doing instance pooling. Instead of creating new instances of a class,
 *           we simply reuse previously created instances.
 */

public class Pool<T> {
    public interface PoolObjectFactory<T> {
        public T createObject();
    }

    private final List<T> freeObjects;
    private final PoolObjectFactory<T> factory;
    private final int maxSize;

    public Pool(PoolObjectFactory<T> factory, int maxSize) {
        this.factory = factory;
        this.maxSize = maxSize;
        this.freeObjects = new ArrayList<T>(maxSize);
    }

    public T newObject() {
        T object = null;

        if (freeObjects.size() == 0)
            object = factory.createObject();
        else
            object = freeObjects.remove(freeObjects.size() - 1);

        return object;
    }

    public boolean free(T object) {
        if ((object != null) && (freeObjects.size() < maxSize)) {
            freeObjects.add(object);
        }

        return true;
    }

    public void clear() {
        freeObjects.clear();
    }
}
