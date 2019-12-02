package me.android.library.common.store;

import java.util.List;

import me.java.library.common.Identifiable;
import me.java.library.common.service.Serviceable;

/**
 * 实体管理接口
 *
 * @param <T>
 * @param <ID>
 * @author sylar
 */
public interface IStoreService<T extends Identifiable<ID>, ID> extends Serviceable {

    long count();

    boolean containsId(ID id);

    T queryById(ID id);

    List<T> queryAll();

    boolean add(T t);

    boolean delete(T t);

    boolean update(T t);

    T getDefault();

    void setDefault(T t);

}