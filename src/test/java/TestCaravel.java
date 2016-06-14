import com.metricinsights.collector.plugin.ExternalReportsListResponse;
import com.metricinsights.collector.plugin.QueryRequest;
import com.metricinsights.collector.plugin.caravel.CaravelPlugin;
import com.metricinsights.test.TestCategories;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertTrue;

/**
 * @author NickVeremeichyk
 * @since 2016-06-14.
 */
@Category(TestCategories.Standalone.class)
public class TestCaravel {

    @Test
    public void testGetObjects() {
        QueryRequest request = new QueryRequest();
        request.setQuery("command = getObjects");
        request.setPluginProperty("server", "http://127.0.0.1:8088");
        request.setPluginProperty("username", "admin");
        request.setPluginProperty("password", "Ins1ght!");
        request.setPluginProperty("path_to_phantom_js", "phantomjs-2.0.0-macosx/bin/phantomjs");

        CaravelPlugin plugin = new CaravelPlugin();
        plugin.validate(request);
        ExternalReportsListResponse response = (ExternalReportsListResponse) plugin.executeQuery(request);

    }


    @Test
    public void testGetObjects1() {
        QueryRequest request = new QueryRequest();
        request.setQuery("command = getObjects");
        request.setPluginProperty("server", "https://oreo.metricinsights.com");
        request.setPluginProperty("username", "admin");
        request.setPluginProperty("password", "Ins1ght!");
        request.setPluginProperty("path_to_phantom_js", "phantomjs-2.0.0-macosx/bin/phantomjs");

        CaravelPlugin plugin = new CaravelPlugin();
        plugin.validate(request);
        ExternalReportsListResponse response = (ExternalReportsListResponse) plugin.executeQuery(request);

    }



}
