package com.metricinsights.collector.plugin.caravel;

import com.metricinsights.collector.plugin.settings.PluginSettings;
import com.metricinsights.collector.plugin.settings.di.Property;
import com.metricinsights.collector.plugin.settings.validation.Validate;

/**
 * @author NickVeremeichyk
 * @since 2016-06-13.
 */
public class CaravelSettings extends PluginSettings {
    @Property("path_to_phantom_js")
    @Validate(regex = "(.+)\\/([^\\/]+)")
    private String pathToPhantomJS;

    public String getPathToPhantomJS() {
        return pathToPhantomJS;
    }

    public void setPathToPhantomJS(String pathToPhantomJS) {
        this.pathToPhantomJS = pathToPhantomJS;
    }
}
