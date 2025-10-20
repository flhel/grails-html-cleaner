package grails.plugin.htmlcleaner

import grails.testing.services.ServiceUnitTest
import org.jsoup.safety.Safelist
import spock.lang.Specification


class SafelistBuilderSpec extends Specification implements ServiceUnitTest<SafelistBuilderSpec> {

	private SafelistBuilder builder = new SafelistBuilder()

	def confSuccess = {
		safelist("sample") {
			startwith "basic"
			allow "b", "p", "img"
			allow("a") {
				attributes "class", "style", "href"
                protocols attribute: 'href', value: 'http'
				enforce attribute:"rel", value:"nofollow"
			}
		}

		safelist("sample2") {
			startwith "none"
			allow "b", "p", "img"
			allow("a") {
				attributes "class", "style", "href"
                protocols attribute: 'href', value: [ 'http', 'https' ]
				enforce attribute:"rel", value:"nofollow"
			}
		}

		safelist("sample3") {
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

	def confSafelistStartsWithUndefinedSafelist = {
		safelist("sample") {
			startwith "undefined"
			allow "b", "p", "img"
		}
	}

	def confSafelistWithReservedName = {
		safelist("none") {
			startwith "basic"
			allow "b", "p", "img"
		}
    }

	void "Build safelist"() {
        given:
		Integer numCalled = 0

        // Need to verify addProtocols() getting called properly, but no get method available on Safelists. MetaClass gets rolled back so should not leak.
        Safelist.metaClass.addProtocols = { String tagName, String attribute, String protocol ->
            assert tagName == 'a'
            assert attribute == 'href'
            assert protocol in [ 'http', 'https']
            numCalled++
        }

        when:
        Map safeLists = builder.build(confSuccess)

        then:
		safeLists
		safeLists['sample']
		safeLists['sample2']
		safeLists['sample3']
        numCalled == 5 // One for each valid protocol in the three sample configs.
	}

	void "Build safelist with undefined safelist"() {
        when:
        builder.build(confSafelistStartsWithUndefinedSafelist)

        then:
        RuntimeException ex = thrown()
        ex.message == "Safelist [undefined] is not defined"
	}

	void "Build safelist with reserved names"() {
        when:
        builder.build(confSafelistWithReservedName)

        then:
        RuntimeException ex = thrown()
        ex.message == "Safelist name [none] is reserved"
	}
}
