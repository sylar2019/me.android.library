package me.android.library.utils.ormlite;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.j256.ormlite.dao.Dao;

import java.util.List;
import java.util.Map;

import me.android.library.common.service.AbstractService;
import me.android.library.common.utils.PackageUtils;

/**
 * Created by sylar on 15/7/6.
 */
abstract public class AbstractDaoService extends AbstractService implements DaoService {

    private static final String TAG = DaoHelper.TAG;

    protected AbsDbHelper dbHelper;
    protected Map<String, Dao<?, ?>> daos = Maps.newHashMap();

    public AbstractDaoService() {
        DaoHelper.init(this);
    }

    @Override
    public String getDbName() {
        List<String> strings = Splitter.on("").splitToList(cx.getPackageName());
        return strings.get(strings.size() - 1);
    }

    @Override
    public int getDbVersion() {
        return PackageUtils.getVersionCode(cx);
    }

    @Override
    public <T, ID> Dao<T, ID> getDao(Class<T> clazz) {
        if (dbHelper == null) {
            dbHelper = getDbHelper();
        }

        String key = clazz.getName();
        if (daos.containsKey(key)) {
            return (Dao<T, ID>) daos.get(key);
        } else {
            try {
                Dao<T, ID> dao = dbHelper.getDao(clazz);
                daos.put(key, dao);
                return dao;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
