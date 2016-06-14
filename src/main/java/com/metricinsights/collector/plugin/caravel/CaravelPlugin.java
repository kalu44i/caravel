package com.metricinsights.collector.plugin.caravel;

import com.metricinsights.collector.plugin.*;
import com.metricinsights.collector.plugin.caravel.phantom.PJSClient;
import com.metricinsights.collector.plugin.reflection.Plugin;
import com.metricinsights.collector.plugin.settings.CommandType;
import com.metricinsights.collector.plugin.settings.SettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;

/**
 * @author NickVeremeichyk
 * @since 2016-06-13.
 */

@Plugin("caravel")
public class CaravelPlugin extends QueryPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(CaravelPlugin.class);

    CaravelSettings settings = null;

    public CaravelPlugin() {
    }

    @Override
    public QueryPlugin getInstance(String jobType) {
        LOGGER.debug("Version: " + getPluginVersion());
        if (jobType.equals(getPluginName())) {
            return new CaravelPlugin();
        }
        return null;
    }

    @Override
    public ServiceResponse executeQuery(QueryRequest request) {
        ServiceResponse response = null;
        try {
            if (settings == null) {
                settings = getSettings(request);
            }
            PJSClient client = new PJSClient(settings);
            final CommandType command = settings.getCommand();

            switch (command) {
                case getObjects:
                    response = client.getObjects();
                case getImage:
                    BufferedImage bi = client.getImage();
//                    ImageIO.write(bi, "png", new File("test.png"));
                    response = new MediaResponse(bi, ContentType.PNG);
                    break;
            }

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return response;
    }

    @Override
    public void validate(QueryRequest request) throws IllegalArgumentException {
        try {
            settings = getSettings(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CaravelSettings getSettings(QueryRequest request) throws Exception {
        CaravelSettings caravelSettings = null;
        if (settings == null) {
            try {
                caravelSettings = SettingsFactory.create(CaravelSettings.class, request, this);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        return caravelSettings;
    }
}
