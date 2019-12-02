package me.android.library.common.store;

import android.util.Log;

import com.google.common.base.Preconditions;

import java.util.List;

import me.android.library.common.service.AbstractService;
import me.java.library.common.Identifiable;

abstract public class AbstractStoreService<Pojo extends Identifiable<ID>, ID> extends
        AbstractService implements IStoreService<Pojo, ID> {

    final static protected String TAG = "store";

    protected Pojo defPojo;

    public AbstractStoreService() {
    }

    // -------------------------------------------------------------------------------
    // public
    // -------------------------------------------------------------------------------

    public boolean isEmpty() {
        return count() == 0;
    }

    public boolean containsPojo(Pojo pojo) {
        checkPojo(pojo);

        return containsId(pojo.getId());
    }

    public Pojo queryByIndex(int index) {
        List<Pojo> list = queryAll();
        if (list.size() > index)
            return list.get(index);
        else
            return null;
    }

    public void batchAdd(List<Pojo> list) {
        if (list == null || list.size() == 0)
            return;

        for (Pojo pojo : list) {
            add(pojo);
        }
    }

    public void batchDelete(List<Pojo> list) {
        if (list == null || list.size() == 0)
            return;

        for (Pojo pojo : list) {
            delete(pojo);
        }
    }

    public void batchUpdate(List<Pojo> list) {
        if (list == null || list.size() == 0)
            return;

        for (Pojo pojo : list) {
            update(pojo);
        }
    }

    public void clearAll() {
        List<Pojo> list = queryAll();
        batchDelete(list);

        return;
    }

    // -------------------------------------------------------------------------------
    // IPojoManager
    // -------------------------------------------------------------------------------

    @Override
    public long count() {
        List<Pojo> list = queryAll();
        if (list != null)
            return list.size();
        else
            return 0;
    }

    @Override
    public boolean containsId(ID id) {
        Preconditions.checkNotNull(id);

        List<Pojo> list = queryAll();
        if (list != null) {
            for (Pojo pojo : list) {
                if (pojo.getId().equals(id))
                    return true;
            }
        }
        return false;
    }

    @Override
    public Pojo queryById(ID id) {
        Preconditions.checkNotNull(id);

        List<Pojo> list = queryAll();
        if (list != null) {
            for (Pojo pojo : list) {
                if (pojo.getId().equals(id))
                    return pojo;
            }
        }
        return null;
    }

    @Override
    public Pojo getDefault() {
        return defPojo;
    }

    @Override
    public void setDefault(Pojo pojo) {
        defPojo = pojo;
        onPojoSelected(defPojo);
    }

    // -------------------------------------------------------------------------------
    // onEvent
    // -------------------------------------------------------------------------------

    protected void onPojoAdded(Pojo pojo) {
        onCollectionChanged();
    }

    protected void onPojoDeleted(Pojo pojo) {
        onCollectionChanged();
    }

    protected void onPojoUpdated(Pojo pojo) {

    }

    protected void onPojoSelected(Pojo pojo) {
    }

    protected void onCollectionChanged() {
        if (defPojo == null || !containsId(defPojo.getId())) {
            setInternalDefault();
        }
    }

    // -------------------------------------------------------------------------------
    // protected
    // -------------------------------------------------------------------------------

    protected void onException(Throwable t) {
        if (t != null) {
            if (t.getMessage() != null)
                Log.w(TAG, t.getMessage());
            else
                t.printStackTrace();
        }
    }

    protected void checkPojo(Pojo pojo) {
        Preconditions.checkNotNull(pojo);
        Preconditions.checkNotNull(pojo.getId());
    }

    protected void setInternalDefault() {
        if (count() > 0) {
            setDefault(queryByIndex(0));
        } else {
            setDefault(null);
        }
    }

}
