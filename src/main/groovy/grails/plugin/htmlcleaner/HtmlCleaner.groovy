package grails.plugin.htmlcleaner

import grails.core.GrailsApplication
import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist
import org.springframework.beans.factory.InitializingBean

class HtmlCleaner implements InitializingBean {

    String defaultWhiteList
    GrailsApplication grailsApplication

    private final Map<String, Whitelist> whitelists = [:]

    void afterPropertiesSet() throws Exception {
        defaultWhiteList = pluginConfig?.defaultWhiteList
        buildWhitelists()
    }

    /**
     *
     * @param html
     * @param whitelist
     * @return
     */
    String cleanHtml(String html, String whitelist = '') {
        if (!html) {
            return html
        }
        if (!whitelist) {
            whitelist = defaultWhiteList
        }
        if (!whitelist) {
            throw new RuntimeException("Default Whitelist is not specified in configuration")
        }

        if (!whitelists[whitelist]) {
            throw new RuntimeException("Whitelist [${whitelist}] is not defined")
        }
        Jsoup.clean(html, whitelists[whitelist])
    }

    // PRIVATE

    private void buildWhitelists() {
        //Clear all existing whitelists
        whitelists.clear()

        def whitelistsClosure = pluginConfig?.whitelists
        if (whitelistsClosure && whitelistsClosure instanceof Closure) {
            WhitelistBuilder builder = new WhitelistBuilder()
            whitelists.putAll(builder.build(whitelistsClosure))
        }

        //Add default whitelists
        whitelists['none'] = Whitelist.none()
        whitelists['basic'] = Whitelist.basic()
        whitelists['simpleText'] = Whitelist.simpleText()
        whitelists['basicWithImages'] = Whitelist.basicWithImages()
        whitelists['relaxed'] = Whitelist.relaxed()
    }

    private def getPluginConfig() {
        grailsApplication.config.grails?.plugin?.htmlcleaner
    }

}
