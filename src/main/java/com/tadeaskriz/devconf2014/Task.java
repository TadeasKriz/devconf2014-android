package com.tadeaskriz.devconf2014;

import org.jboss.aerogear.android.RecordId;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class Task {

    @RecordId
    private Long id;
    private String text;
    private boolean done;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
