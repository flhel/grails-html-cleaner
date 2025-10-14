package grails.plugin.htmlcleaner

import org.jsoup.safety.Safelist

class SafelistBuilder {

	private static final List RESERVED_WHITELISTS = ['none', 'simpleText', 'basic', 'basicWithImages', 'relaxed']
	private final Map safeLists = [:]

	private Safelist currentSafelist
	private String currentTag

	Map<String, Safelist> build(Closure c) {
		if(c) {
			c.delegate = this
			c.resolveStrategy = Closure.DELEGATE_ONLY
			c.call()
		}
		return safeLists
	}

	def safelist(String name, Closure c) {
		if(!name) {
			throw new RuntimeException("Safelist must have a name")
		}
		if(RESERVED_WHITELISTS.contains(name)) {
			throw new RuntimeException("Safelist name [${name}] is reserved")
		}
		if(c) {
			c.delegate = this
			c.resolveStrategy = Closure.DELEGATE_ONLY
			c.call()
			safeLists[name] = currentSafelist
		}
	}

	def startwith(String name) {
		currentSafelist = null
		switch (name) {
			case "basic":
				currentSafelist = Safelist.basic()
				break
			case "none":
				currentSafelist = Safelist.none()
				break
			case "simpleText":
				currentSafelist = Safelist.simpleText()
				break
			case "basicWithImages":
				currentSafelist = Safelist.basicWithImages()
				break
			case "relaxed":
				currentSafelist = Safelist.relaxed()
				break
			default:
				currentSafelist = safeLists[name]
		}
		if(!currentSafelist) {
			throw new RuntimeException("Safelist [${name}] is not defined")
		}
	}

	def allow(String[] tags) {
		tags.each {
			currentSafelist.addTags(it)
		}
	}

	def allow(String name, Closure c) {
		if(name) {
			currentSafelist.addTags(name)
			currentTag = name
		}
		if(c) {
			c.delegate = this
			c.resolveStrategy = Closure.DELEGATE_ONLY
			c.call()
		}
	}

	def attributes(String[] attrs) {
		attrs.each {
			currentSafelist.addAttributes(currentTag, it)
		}
	}

	def enforce(Map attr) {
		if(attr) {
			currentSafelist.addEnforcedAttribute(currentTag, attr.attribute, attr.value)
		}
	}

    def protocols(Map map) {
        if (!map?.attribute || !map.value) {
            return
        }

        List values = map.value instanceof String ? [ map.value ] : map.value

        values.each { String protocol ->
            currentSafelist.addProtocols(currentTag, map.attribute, protocol)
        }
    }
}
