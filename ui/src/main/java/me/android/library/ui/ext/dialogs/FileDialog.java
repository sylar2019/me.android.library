package me.android.library.ui.ext.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.android.library.common.utils.ToastUtils;
import me.android.library.common.utils.ViewUtils;
import me.android.library.ui.R;
import me.android.library.ui.ext.OkCancelCallback;
import me.android.library.ui.ext.adapters.ExtBaseAdapter;

@SuppressLint("InflateParams")
public class FileDialog {

    static final public String sParent = "..";

    static public AlertDialog openFile(Context cx, String title,
                                       OpenFileCallback callback,
                                       String... filters) {
        AlertDialog.Builder builder = DialogHelper.newBuilder(cx, title);
        final AlertDialog dlg = builder.create();

        final Adapter adapter = new Adapter(cx);
        ListView listView = new ListView(cx);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            FileItem fi = adapter.getEntity(position);
            if (fi instanceof ParentFileItem) {
                adapter.load(fi.file.getParentFile(), filters);
            } else {
                if (fi.isDirectory()) {
                    adapter.load(fi.file, filters);
                } else {
                    if (callback != null) {
                        dlg.dismiss();
                        callback.onOpen(fi.file);
                    }
                }
            }
        });

        dlg.setView(listView);
        adapter.load(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filters);
        dlg.show();

        return dlg;
    }

    static public AlertDialog saveFile(Context cx, String title, String defaultName,
                                       final SaveFileCallback callback) {
        final Adapter adapter = new Adapter(cx);
        adapter.load(Environment.getExternalStorageDirectory());

        View view = LayoutInflater.from(cx).inflate(R.layout.dialog_file, null);
        ListView listView = view.findViewById(R.id.lvFiles);
        final EditText edtFileName = view.findViewById(R.id.edtFileName);
        edtFileName.setText(defaultName);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                FileItem fi = (FileItem) adapter.getItem(position);
                if (fi instanceof ParentFileItem) {
                    adapter.load(fi.file.getParentFile());
                } else {
                    if (fi.isDirectory()) {
                        adapter.load(fi.file);
                    }
                }
            }
        });

        return DialogHelper.showQuestView(cx, title, view, new OkCancelCallback<Dialog>() {
            @Override
            public void onOK(Dialog dialog) {
                ViewUtils.setDialogShowField(dialog, true);
                String fileName = edtFileName.getText().toString();
                if (Strings.isNullOrEmpty(fileName)) {
                    ViewUtils.setDialogShowField(dialog, false);
                    ToastUtils.showShort("file's name is empty");
                } else {
                    fileName = adapter.getCurrent().getPath() + "/"
                            + fileName;
                    if (callback != null) {
                        callback.onSave(fileName);
                    }
                }
            }

            @Override
            public void onCancel(Dialog dialog) {

            }
        });

    }

    public interface SaveFileCallback {
        void onSave(String fileName);
    }

    public interface OpenFileCallback {
        void onOpen(File file);
    }

    static class FileItem {
        File file;

        public FileItem(File file) {
            this.file = file;
        }

        public String getName() {
            return file.getName();
        }

        public String getDesc() {
            return file.getPath();
        }

        public boolean isDirectory() {
            return file.isDirectory();
        }

        @Override
        public String toString() {
            return getDesc();
        }
    }

    static class ParentFileItem extends FileItem {
        public ParentFileItem(File file) {
            super(file);
        }

        @Override
        public String getName() {
            return sParent;
        }

        @Override
        public String getDesc() {
            return "返回上级目录";
        }
    }

    static class Adapter extends ExtBaseAdapter<FileItem> {

        Context cx;
        File file;

        public Adapter(Context cx) {
            this.cx = cx;
        }

        public File getCurrent() {
            return file;
        }

        private String getRegex(String... filters) {
            List<String> list = Lists.newArrayList();
            for (String filter : filters) {
                filter = filter.replace("*", "").replace(".", "");
                list.add("." + filter);
            }

            String str = Joiner.on("|").join(list).toLowerCase();
            return String.format(".+(%s)$", str);
        }

        public void load(File file, String... filters) {
            File[] files = file.listFiles();
            if (files == null)
                return;

            this.file = file;

            List<FileItem> list = Lists.newArrayList();
            loadData(list);

            for (File f : files) {
                if (!f.isHidden()) {
                    if (f.isDirectory()) {
                        list.add(new FileItem(f));
                    } else if (f.isFile()) {
                        if (filters != null) {
                            String reg = getRegex(filters);
                            Matcher matcher = Pattern.compile(reg).matcher(f.getName());
                            if (matcher.matches()) {
                                list.add(new FileItem(f));
                            }
                        } else {
                            list.add(new FileItem(f));
                        }
                    }
                }
            }

            Collections.sort(list, (lhs, rhs) -> {
                int i = 0;
                if (lhs.isDirectory() && rhs.isDirectory()) {
                    i = ComparisonChain.start()
                            .compare(lhs.getName(), rhs.getName()).result();
                } else {
                    if (lhs.isDirectory()) {
                        i = -1;
                    } else {
                        i = 1;
                    }
                }
                return i;
            });

            if (file.getParentFile() != null) {
                list.add(0, new ParentFileItem(file));
            }

            loadData(list);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHoler vh;
            if (convertView == null) {
                convertView = LayoutInflater.from(cx).inflate(
                        R.layout.dialog_file_item, parent, false);
                vh = new ViewHoler(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHoler) convertView.getTag();
            }

            FileItem fi = list.get(position);
            vh.showItem(fi);

            return convertView;
        }

        class ViewHoler {

            ImageView img;
            TextView txtName;
            TextView txtDesc;

            public ViewHoler(View view) {
                img = view.findViewById(R.id.imgItem);
                txtName = view.findViewById(R.id.txtName);
                txtDesc = view.findViewById(R.id.txtDesc);
            }

            public void showItem(FileItem item) {
                img.setImageResource(item.isDirectory() ? R.mipmap.ic_filedialog_folder
                        : R.mipmap.ic_filedialog_file);
                txtName.setText(item.getName());
                txtDesc.setText(item.getDesc());
            }
        }
    }
}
