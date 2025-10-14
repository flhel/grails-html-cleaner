Html Cleaner Grails Plugin
==========================

Updated to work with Grails 6 and newer versions of Jsoup 

New Jsoup versions (≥ 1.14.1): ❌ org.jsoup.safety.Whitelist → ✅ org.jsoup.safety.Safelist

# Introduction

The **Html Cleaner Plugin** is a whitelist based html sanitizer, based on [Jsoup](http://jsoup.org). 
This is a port to Grails3 of the Grails1 plugin, originally written by Sudhir Nimavat.

It provides:
* a DSL to define whitelists,
* **HtmlCleaner** - A Spring bean which provides a method *cleanHtml()* to sanitize html,
* **htmlCleanerTagLib** - A taglib to sanitize html.

Note: Html cleaner is not just a sanitizer, it cleans ill-formed user supplied html and produces a well formed xml.

# Installation

Declare the plugin dependency in the _build.gradle_ file, as shown here:

```groovy
repositories {
    ...
    maven { url "http://dl.bintray.com/agorapulse/plugins" }
}
dependencies {
    ...
    compile "org.grails.plugins:html-cleaner:1.1"
}
```

# Config

Following whitelists are available by default and does not need any configuration:
- none
- simpleText
- basic
- basicWithImages
- relaxed

You can define default white list in your _grails-app/conf/application.yml_:

```yml
grails:
    plugin:
        htmlcleaner:
            defaultWhiteList: basic
```

See below to define custom whitelists.

# Usage

Let's say you have a form with a text area, but you don't want to allow any html. You can clean the user supplied text with whitelist none and it will stripe out all the html.

```groovy
import grails.plugin.htmlcleaner.HtmlCleaner

class FooController {

    HtmlCleaner htmlCleaner

    def save = {
       String cleaned = htmlCleaner.cleanHtml(params.textArea, 'none') 
   }
}
```

Or in a service:

```groovy
import grails.plugin.htmlcleaner.HtmlCleaner

class FooService {

    HtmlCleaner htmlCleaner

    def foo(unsafe) {
        String cleaned = htmlCleaner.cleanHtml(unsafe, 'none')
    }
}
```

You can also allow basic html as per basic whitelist.

```groovy
def cleaned = htmlCleaner.cleanHtml(unsafe, 'basic')
```

The plugin also provides a taglib.

```html
<hc:cleanHtml html="${domainInstance.description}" whitelist="basic"/>
```

# Defining custom whitelists

Plugin provides a DSL to define custom whitelists in configuration.
Define a custom whitelist sample that will allow just *b*, *i*, *p* and *span* tags.

application.groovy

```
grails {
    plugin {
        htmlcleaner {
            whitelists = {
                whitelist("sample") {
                    startwith "none"
                    allow "b", "p", "i", "span"
                }
            }
        }
    }
}
```

The above configuration would define a whitelist with name sample that builds on top of whitelist none and allows additional tags *b*, *i*, *p* and *span*.

A whitelist can start with any of the default whitelists or A whitelist can start with any custom whitelists that are defined earlier in configuration as well, but it must start with another whitelist.

Define a whitelist sample2 that starts with whitelist sample we defined above and allows tag *a* with just one attribute href and puts rel="nofollow"

```
grails {
    plugin {
        htmlcleaner {
            whitelists = {
                whitelist("sample2") {
                    startwith "sample"
                    allow("a") {
                        attributes "href"
                        enforce attribute:"rel", value:"nofollow"
                    }
                }
            }
        }
    }
}
```

Define a whitelist basic-with-tables that starts with whitelist basic and allows tables.

```
grails {
    plugin {
        htmlcleaner {
            whitelists = {
                whitelist("basic-with-tables") {
                    startwith "basic"
                    allow "table", "tr", "td"
                }
            }
        }
    }
}
```

Restricting attributes

```
grails {
    plugin {
        htmlcleaner {
            whitelists = {
                whitelist("sample") {
                    allow("div") {
                        attributes "id", "class"
                    }
                }
            }
        }
    }
}
```

Enforcing attributes - An enforced attribute will always be added to the element. If the element already has the attribute set, it will be overridden.

```
grails {
    plugin {
        htmlcleaner {
            whitelists = {
                whitelist("sample") {
                    allow("div") {
                        enforce attribute:"class", value:"block"
                    }
                }
            }
        }
    }
}
```

Defining multiple whitelists

```
grails {
    plugin {
        htmlcleaner {
            whitelists = {
                whitelist("sample") {
                    startwith "none"
                    allow "b", "p", "span"
                }
                whitelist("sample-with-anchor") {
                    startwith "sample"
                    allow("a") {
                        attributes "href"
                        enforce attribute:"rel", value:"nofollow"
                    }
                }
        
                whitelist("basic-with-tables") {
                    startwith "basic"
                    allow "table", "tr", "td"
                }
        
            }
        }
    }
}
```

# Bugs

To report any bug, please use the project [Issues](http://github.com/agorapulse/grails-html-cleaner/issues) section on GitHub.
