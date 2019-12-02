package me.android.library.common.store;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import me.java.library.common.Identifiable;

abstract public class AbstractMapStoreService<Pojo extends Identifiable<ID>, ID> extends
        AbstractStoreService<Pojo, ID> {

    protected Map<ID, Pojo> map = Maps.newLinkedHashMap();

    @Override
    public long count() {
        return map.size();
    }

    @Override
    public boolean containsId(ID id) {
        return map.containsKey(id);
    }

    @Override
    public Pojo queryById(ID id) {
        return map.get(id);
    }

    @Override
    public List<Pojo> queryAll() {
        return Lists.newArrayList(map.values());
    }

    @Override
    public boolean add(Pojo pojo) {
        checkPojo(pojo);
        if (containsPojo(pojo))
            return false;

        map.put(pojo.getId(), pojo);

        onPojoAdded(pojo);
        return true;
    }

    @Override
    public boolean delete(Pojo pojo) {
        checkPojo(pojo);
        if (!containsPojo(pojo))
            return false;

        map.remove(pojo.getId());

        onPojoDeleted(pojo);
        return true;
    }

    @Override
    public boolean update(Pojo pojo) {
        checkPojo(pojo);
        if (!containsPojo(pojo))
            return false;

        map.put(pojo.getId(), pojo);
        onPojoUpdated(pojo);
        return true;
    }

    @Override
    public void clearAll() {
        for (Pojo pojo : map.values()) {
        }

        map.clear();
        onCollectionChanged();
    }

}
