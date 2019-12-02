package me.android.library.ui.ext;

import me.android.library.ui.AbstractActivity;
import me.android.library.ui.R;

public abstract class BaseActivity extends AbstractActivity {

    @Override
    protected void setContentView() {
        setContentView(R.layout.abs_activity);
    }
}
