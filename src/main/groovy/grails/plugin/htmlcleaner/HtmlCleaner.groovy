package grails.plugin.htmlcleaner

import grails.core.GrailsApplication
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import org.springframework.beans.factory.InitializingBean

class HtmlCleaner implements InitializingBean {

    String defaultSafeList
    GrailsApplication grailsApplication

    private final Map<String, Safelist> safelists = [:]

    void afterPropertiesSet() throws Exception {
        defaultSafeList = pluginConfig?.defaultSafeList
        buildSafelists()
    }

    /**
     *
     * @param html
     * @param safelist
     * @return
     */
    String cleanHtml(String html, String safelist = '') {
        if (!html) {
            return html
        }
        if (!safelist) {
            safelist = defaultSafeList
        }
        if (!safelist) {
            throw new RuntimeException("Default Safelist is not specified in configuration")
        }

        if (!safelists[safelist]) {
            throw new RuntimeException("Safelist [${safelist}] is not defined")
        }
        Jsoup.clean(html, safelists[safelist])
    }

    // PRIVATE

    private void buildSafelists() {
        //Clear all existing safelists
        safelists.clear()

        def safelistsClosure = pluginConfig?.safelists
        if (safelistsClosure && safelistsClosure instanceof Closure) {
            SafelistBuilder builder = new SafelistBuilder()
            safelists.putAll(builder.build(safelistsClosure))
        }

        //Add default safelists
        safelists['none'] = Safelist.none()
        safelists['basic'] = Safelist.basic()
        safelists['simpleText'] = Safelist.simpleText()
        safelists['basicWithImages'] = Safelist.basicWithImages()
        safelists['relaxed'] = Safelist.relaxed()
    }

    private def getPluginConfig() {
        grailsApplication.config.grails?.plugin?.htmlcleaner
    }

}
