package wumpus.agent;

import java.util.Collection;
import java.util.HashMap;
public class DefaultDict<K, V> extends HashMap<K, V> {

    private V defaultValue;

    public DefaultDict(V defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public V get(Object key) {
        V returnValue = super.get(key);
        if (returnValue == null) return defaultValue;
        return returnValue;
    }

    public void appendToValue(K key, Object obj) {
        if(!(defaultValue instanceof Collection)) throw new RuntimeException("Value of map is not a list");
        Collection oldValue = (Collection) this.get(key);
        oldValue.add(obj);
        this.put(key, (V) oldValue);
    }
}
