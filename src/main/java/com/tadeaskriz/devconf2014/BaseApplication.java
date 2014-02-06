package com.tadeaskriz.devconf2014;

import android.app.Application;
import org.androidannotations.annotations.EApplication;
import org.jboss.aerogear.android.unifiedpush.PushConfig;
import org.jboss.aerogear.android.unifiedpush.PushRegistrar;
import org.jboss.aerogear.android.unifiedpush.Registrations;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
@EApplication
public class BaseApplication extends Application {

    private PushRegistrar registrar;

    @Override
    public void onCreate() {
        super.onCreate();

        Registrations registrations = new Registrations();

        try {
            PushConfig config = new PushConfig(new URI("https://devconf2014push-detox.rhcloud.com/"), "489342927486");
            config.setVariantID("43d4bd9e-62d5-477b-99f8-efe5de9af3c3");
            config.setSecret("6f98f26b-0c89-4c94-82ea-26b6ee998813");

            registrar = registrations.push("unifiedpush", config);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    public PushRegistrar getRegistrar() {
        return registrar;
    }
}
