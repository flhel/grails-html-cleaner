package grails.plugin.htmlcleaner

import org.jsoup.safety.Whitelist
import spock.lang.Specification


class WhitelistBuilderSpec extends Specification {

	private WhitelistBuilder builder = new WhitelistBuilder()

	def confSuccess = {
		whitelist("sample") {
			startwith "basic"
			allow "b", "p", "img"
			allow("a") {
				attributes "class", "style", "href"
                protocols attribute: 'href', value: 'http'
				enforce attribute:"rel", value:"nofollow"
			}
		}

		whitelist("sample2") {
			startwith "none"
			allow "b", "p", "img"
			allow("a") {
				attributes "class", "style", "href"
                protocols attribute: 'href', value: [ 'http', 'https' ]
				enforce attribute:"rel", value:"nofollow"
			}
		}

		whitelist("sample3") {
			startwith "sample2"
			allow "span"
            allow("a") {
                protocols attribute: 'href', value: 'http'
                protocols attribute: 'href', value: 'https'
                protocols attribute: 'href' // Invalid
                protocols value: 'http' // Invalid
            }
		}
	}

	def confWhitelistStartsWithUndefinedWhitelist = {
		whitelist("sample") {
			startwith "undefined"
			allow "b", "p", "img"
		}
	}

	def confWhitelistWithReservedName = {
		whitelist("none") {
			startwith "basic"
			allow "b", "p", "img"
		}
    }

	void "Build whitelist"() {
        given:
		Integer numCalled = 0

        // Need to verify addProtocols() getting called properly, but no get method available on Whitelists. MetaClass gets rolled back so should not leak.
        Whitelist.metaClass.addProtocols = { String tagName, String attribute, String protocol ->
            assert tagName == 'a'
            assert attribute == 'href'
            assert protocol in [ 'http', 'https']
            numCalled++
        }

        when:
        Map whiteLists = builder.build(confSuccess)

        then:
		whiteLists
		whiteLists['sample']
		whiteLists['sample2']
		whiteLists['sample3']
        numCalled == 5 // One for each valid protocol in the three sample configs.
	}

	void "Build whitelist with undefined whitelist"() {
        when:
        builder.build(confWhitelistStartsWithUndefinedWhitelist)

        then:
        RuntimeException ex = thrown()
        ex.message == "Whitelist [undefined] is not defined"
	}

	void "Build whitelist with reserved names"() {
        when:
        builder.build(confWhitelistWithReservedName)

        then:
        RuntimeException ex = thrown()
        ex.message == "Whitelist name [none] is reserved"
	}
}
