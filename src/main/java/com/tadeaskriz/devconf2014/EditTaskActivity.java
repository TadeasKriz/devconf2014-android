package com.tadeaskriz.devconf2014;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
@EActivity(R.layout.activity_edittask)
@OptionsMenu(R.menu.edit)
public class EditTaskActivity extends Activity {

    public static final int RESULT_DELETE = RESULT_FIRST_USER;
    public static final int RESULT_SAVE = RESULT_DELETE + 1;
    public static final String RESULT_BUNDLE_TASK = "task";

    @ViewById
    EditText text;

    @ViewById
    Button add;

    @Extra
    Task task;

    @AfterViews
    void init() {
        text.setText(task.getText());
    }

    @Click(R.id.save)
    void saveItem() {
        Intent data = new Intent();
        task.setText(text.getText().toString());
        data.putExtra(RESULT_BUNDLE_TASK, task);
        setResult(RESULT_SAVE, data);
        finish();
    }

    @OptionsItem(R.id.action_delete)
    void deleteItem() {
        Intent data = new Intent();
        data.putExtra(RESULT_BUNDLE_TASK, task);
        setResult(RESULT_DELETE, data);
        finish();
    }

}
