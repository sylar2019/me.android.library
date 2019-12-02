package me.android.library.utils.ormlite;

import com.j256.ormlite.dao.Dao;

public interface DaoService {
    String getDbName();

    int getDbVersion();

    AbsDbHelper getDbHelper();

    <T, ID> Dao<T, ID> getDao(Class<T> clazz);
}