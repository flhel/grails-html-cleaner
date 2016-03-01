package grails.plugin.htmlcleaner

import grails.plugins.*

class HtmlCleanerGrailsPlugin extends Plugin {

    def grailsVersion = "3.0.0 > *"

    def title = "Html Cleaner"
    def author = "Benoit Hediard"
    def authorEmail = "ben@benorama.com"
    def description = '''\
Whitelist based html cleaner based on jsoup.
'''
    def profiles = ['web']

    def documentation = "http://grails.org/plugin/html-cleaner"
    def license = "APACHE"
    def organization = [ name: "AgoraPulse", url: "http://www.agorapulse.com/" ]
    def developers = [[name: "Sudhir Nimavat", email: "sudhir_nimavat@yahoo.com"], [name: "Igor Shults", email: "igor.shults@gmail.com"]]
    def issueManagement = [ system: "github", url: "https://github.com/agorapulse/grails-html-cleaner/issues" ]
    def scm = [  url: "https://github.com/agorapulse/grails-html-cleaner" ]

    Closure doWithSpring() {{->
        htmlCleaner(HtmlCleaner) {
            grailsApplication = grailsApplication
        }
    }}

}
