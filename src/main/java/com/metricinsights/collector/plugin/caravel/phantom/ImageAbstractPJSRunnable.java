package com.metricinsights.collector.plugin.caravel.phantom;

import com.metricinsights.collector.plugin.caravel.CaravelSettings;

import java.awt.image.BufferedImage;

/**
 * @author NickVeremeichyk
 * @since 2016-06-13.
 */
public class ImageAbstractPJSRunnable extends AbstractPJSRunnable {

    private BufferedImage image;

    public ImageAbstractPJSRunnable(String url, CaravelSettings settings) {
        this.url = url;
        this.settings = settings;
    }

    @Override
    protected void pullData() throws Exception {

    }

    public BufferedImage getPulledImage() {
        return image;
    }
}
