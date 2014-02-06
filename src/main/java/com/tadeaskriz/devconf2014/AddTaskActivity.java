package com.tadeaskriz.devconf2014;

import android.app.Activity;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
@EActivity(R.layout.activity_addtask)
public class AddTaskActivity extends Activity {


    @ViewById
    EditText text;

    @ViewById
    Button add;

    @Click(R.id.add)
    void addItem() {
        Intent data = new Intent();
        data.putExtra("text", text.getText().toString());
        setResult(RESULT_OK, data);
        finish();
    }

}
