//package me.android.library.ui.ext.views;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.FrameLayout;
//
//import com.handmark.pulltorefresh.library.PullToRefreshBase;
//
//import java.util.List;
//
//import me.android.library.ui.RefreshView;
//import me.android.library.ui.ext.adapters.ExtBaseAdapter;
//import me.android.library.common.utils.ToastUtils;
//import me.java.library.common.Callback;
//
//abstract public class AbsAdapterPtrView<T, V extends android.widget.AbsListView> extends FrameLayout
//        implements RefreshView, AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener2 {
//
//    protected final static int limit = 10;
//
//    protected Context cx;
//    protected ExtBaseAdapter<T> adapter;
//    protected PullToRefreshBase<V> refreshLayout;
//    protected AdapterView adapterView;
//
//    public AbsAdapterPtrView(Context context) {
//        super(context);
//        init(context, null);
//    }
//
//    public AbsAdapterPtrView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init(context, attrs);
//    }
//
//    public AbsAdapterPtrView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init(context, attrs);
//    }
//
//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        onRefresh();
//    }
//
//    void init(Context cx, AttributeSet attrs) {
//        this.cx = cx;
//        refreshLayout = getRefreshLayout();
//        this.addView(refreshLayout);
//
//        refreshLayout.setMode(getPullMode());
//        refreshLayout.setOnRefreshListener(this);
//
//        adapterView = refreshLayout.getRefreshableView();
//        adapterView.setOnItemClickListener(this);
//
//        onBeforeSetAdapter();
//
//        adapter = getAdapter();
//        adapterView.setAdapter(adapter);
//    }
//
//    // ===========================================================================================
//
//    @Override
//    public void onRefresh() {
//        onRefreshWhenPullDown();
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        T foo = (T) adapterView.getAdapter().getItem(i);
//        onClickItem(foo);
//    }
//
//    @Override
//    public void onPullDownToRefresh(PullToRefreshBase pullToRefreshBase) {
//        onRefreshWhenPullDown();
//    }
//
//    @Override
//    public void onPullUpToRefresh(PullToRefreshBase pullToRefreshBase) {
//        onRefreshWhenPullUp();
//    }
//
//    // ===========================================================================================
//
//    abstract protected PullToRefreshBase<V> getRefreshLayout();
//
//    abstract protected ExtBaseAdapter<T> getAdapter();
//
//    abstract protected void onLoad(int start, int limit, Callback<List<T>> callback);
//
//    // ===========================================================================================
//
//    public void onRefreshWhenPullDown() {
//
//        onLoad(0, limit, new Callback<List<T>>() {
//            @Override
//            public void onSuccess(List<T> foos) {
//                adapter.loadData(foos);
//                refreshLayout.onRefreshComplete();
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                refreshLayout.onRefreshComplete();
//                ToastUtils.showThrowable(t);
//            }
//        });
//    }
//
//    public void onRefreshWhenPullUp() {
//
//        int start = adapter.getCount();
//        onLoad(start, limit, new Callback<List<T>>() {
//            @Override
//            public void onSuccess(List<T> foos) {
//                adapter.appendData(foos);
//                refreshLayout.onRefreshComplete();
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                refreshLayout.onRefreshComplete();
//                ToastUtils.showThrowable(t);
//            }
//        });
//    }
//
//    /**
//     * item click
//     */
//    protected void onClickItem(T foo) {
//    }
//
//    /**
//     * 下拉刷新模式
//     */
//    protected PullToRefreshBase.Mode getPullMode() {
//        return PullToRefreshBase.Mode.PULL_FROM_START;
//    }
//
//    /**
//     * 在为listview设置adapter之前，还可以设置listview的head或foot
//     */
//    protected void onBeforeSetAdapter() {
//
//    }
//
//}
