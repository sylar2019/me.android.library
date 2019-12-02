package me.android.library.utils.greendao;

import android.content.Context;
import android.database.Cursor;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import me.android.library.common.service.AbstractService;

/**
 * File Name             :  AbstractDaoService
 *
 * @author :  sylar
 * Create                :  2019-11-25
 * Description           :
 * Reviewed By           :
 * Reviewed On           :
 * Version History       :
 * Modified By           :
 * Modified Date         :
 * Comments              :
 * CopyRight             : COPYRIGHT(c) allthings.vip  All Rights Reserved
 * *******************************************************************************************
 */
@SuppressWarnings("unchecked")
public abstract class AbstractDaoService extends AbstractService {

    protected AbstractDaoSession daoSession;

    @Override
    public void init(Context cx, Object... params) {
        super.init(cx, params);
        daoSession = createDaoSession(cx);
    }

    protected abstract AbstractDaoSession createDaoSession(Context cx);

    @Override
    public void dispose() {
        super.dispose();
        daoSession = null;
    }

    public AbstractDaoSession getDaoSession() {
        return daoSession;
    }

    /**
     * 原生sql文查询
     *
     * @param sql
     * @param selectionArgs
     * @return
     */
    public Cursor rawQuery(final String sql, final String[] selectionArgs) {
        return daoSession.getDatabase().rawQuery(sql, selectionArgs);
    }

    /**
     * 主键查询实体
     *
     * @param entityClass
     * @param key
     * @param <T>
     * @param <K>
     * @return
     */
    public <T, K> T query(Class<T> entityClass, final K key) {
        AbstractDao<T, K> dao = (AbstractDao<T, K>) daoSession.getDao(entityClass);
        return dao.load(key);
    }

    /**
     * 条件查询实体集合
     *
     * @param entityClass
     * @param where
     * @param selectionArgs
     * @param <T>
     * @return
     */
    public <T> List<T> query(Class<T> entityClass, String where, Object... selectionArgs) {
        AbstractDao<T, ?> dao = (AbstractDao<T, ?>) daoSession.getDao(entityClass);
        QueryBuilder<T> builder = dao.queryBuilder().where(
                new WhereCondition.StringCondition(where, selectionArgs));
        return builder.list();
    }

    /**
     * 全量查询实体集合
     *
     * @param entityClass
     * @param <T>
     * @return
     */
    public <T> List<T> queryAll(Class<T> entityClass) {
        return daoSession.loadAll(entityClass);
    }


    /**
     * 实体数量查询
     *
     * @param entityClass
     * @param <T>
     * @return
     */
    public <T> long queryCount(Class<T> entityClass) {
        AbstractDao<T, ?> dao = (AbstractDao<T, ?>) daoSession.getDao(entityClass);
        return dao.count();
    }

    /**
     * 获取 QueryBuilder 对象
     *
     * @param entityClass
     * @param <T>
     * @return
     */
    public <T> QueryBuilder<T> queryBuilder(Class<T> entityClass) {
        return daoSession.queryBuilder(entityClass);
    }

    /**
     * 条件删除实体
     *
     * @param entityClass
     * @param where
     * @param selectionArgs
     * @param <T>
     */
    public <T> void queryDelete(Class<T> entityClass, String where, Object... selectionArgs) {
        AbstractDao<T, ?> dao = (AbstractDao<T, ?>) daoSession.getDao(entityClass);
        QueryBuilder<T> builder = dao.queryBuilder().where(
                new WhereCondition.StringCondition(where, selectionArgs));
        builder.buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * 新增实体
     *
     * @param entity
     * @param <T>
     * @return
     */
    public <T> long insert(final T entity) {
        return daoSession.insert(entity);
    }

    /**
     * 更新实体
     *
     * @param entity
     * @param <T>
     */
    public <T> void update(final T entity) {
        daoSession.update(entity);
    }

    /**
     * 删除实体
     *
     * @param entity
     * @param <T>
     */
    public <T> void delete(final T entity) {
        daoSession.delete(entity);
    }

    /**
     * 新增实体集合
     *
     * @param list
     * @param <T>
     * @throws Exception
     */
    public <T> void insertInTx(final List<T> list) throws Exception {
        daoSession.callInTx(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Iterator<T> iterator = list.iterator();
                while (iterator.hasNext()) {
                    T entity = iterator.next();
                    daoSession.insert(entity);
                }
                return null;
            }
        });
    }

    /**
     * 更新实体集合
     *
     * @param list
     * @param <T>
     * @throws Exception
     */
    public <T> void updateInTx(final List<T> list) throws Exception {
        daoSession.callInTx(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Iterator<T> iterator = list.iterator();
                while (iterator.hasNext()) {
                    T entity = iterator.next();
                    daoSession.update(entity);
                }
                return null;
            }
        });
    }

    /**
     * 删除实体集合
     *
     * @param list
     * @param <T>
     * @throws Exception
     */
    public <T> void deleteInTx(final List<T> list) throws Exception {
        daoSession.callInTx(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Iterator<T> iterator = list.iterator();
                while (iterator.hasNext()) {
                    T entity = iterator.next();
                    daoSession.delete(entity);
                }
                return null;
            }
        });
    }

    /**
     * 删除所有实体集合
     *
     * @param entityClass
     * @param <T>
     */
    public <T> void deleteAll(Class<T> entityClass) {
        daoSession.deleteAll(entityClass);
    }


    /**
     * 异步事物处理
     *
     * @param runnable
     */
    public void runInTx(Runnable runnable) {
        daoSession.runInTx(runnable);
    }

    /**
     * 同步事物处理
     *
     * @param callable
     * @param <V>
     * @return
     * @throws Exception
     */
    public <V> V callInTx(Callable<V> callable) throws Exception {
        V result = daoSession.callInTx(callable);
        return result;
    }


    /**
     * 分页查询实体集合
     *
     * @param entityClass
     * @param pageIndex
     * @param pageSize
     * @param <T>
     * @return
     */
    public <T> List<T> queryPage(Class<T> entityClass, int pageIndex, int pageSize) {
        AbstractDao<T, ?> dao = (AbstractDao<T, ?>) daoSession.getDao(entityClass);
        QueryBuilder<T> builder = dao.queryBuilder().offset(pageIndex * pageSize).limit(pageSize);
        return builder.list();
    }

    /**
     * 分页条件查询实体集合
     *
     * @param entityClass
     * @param pageIndex
     * @param pageSize
     * @param where
     * @param selectionArgs
     * @param <T>
     * @return
     */
    public <T> List<T> queryPage(Class<T> entityClass,
                                 int pageIndex,
                                 int pageSize,
                                 String where,
                                 Object... selectionArgs) {
        AbstractDao<T, ?> dao = (AbstractDao<T, ?>) daoSession.getDao(entityClass);
        QueryBuilder<T> builder = dao.queryBuilder()
                .where(new WhereCondition.StringCondition(where, selectionArgs))
                .offset(pageIndex * pageSize)
                .limit(pageSize);
        return builder.list();
    }


    /**
     * 分页条件查询实体集合
     *
     * @param entityClass
     * @param pageIndex
     * @param pageSize
     * @param where
     * @param selectionArgs
     * @param <T>
     * @return
     */
    public <T> List<T> queryPage(Class<T> entityClass,
                                 int pageIndex,
                                 int pageSize,
                                 Property properties,
                                 String where,
                                 Object... selectionArgs) {
        AbstractDao<T, ?> dao = (AbstractDao<T, ?>) daoSession.getDao(entityClass);
        QueryBuilder<T> builder = dao.queryBuilder()
                .orderDesc(properties)
                .where(new WhereCondition.StringCondition(where, selectionArgs))
                .offset(pageIndex * pageSize)
                .limit(pageSize);
        return builder.list();
    }

}
