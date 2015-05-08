package org.bessle.neo4j.proxy

import org.apache.http.Header
import org.springframework.http.HttpHeaders
import groovy.util.logging.Slf4j


/**
 * Created by uwe on 07.05.15.
 */
@Slf4j
class HttpUtil {

    static void copyResponseHeaders(Iterable<Header> inputRequestHeaders, HttpHeaders copiedRequestHeaders, List blacklist = [], Map additionalHeaders = [:]) {
        log.debug("copyResponseHeaders(... , ... , ${blacklist} , ${additionalHeaders})")
        inputRequestHeaders.each() { Header header ->
            if ( header.name in blacklist ) {
                log.debug("copyResponseHeaders(): do not copy header ${header.name}: ${header.value}")
            } else {
                copiedRequestHeaders.add(header.name, header.value)
                //log.debug("copyResponseHeaders(): copy header ${header.name}: ${header.value}")
            }
        }
        additionalHeaders.each { String name, String value ->
            log.debug("copyRequestHeaders(): add additional header ${name}: ${value}")
            copiedRequestHeaders.add(name, value)
        }
        copiedRequestHeaders.each() { def headerName, def headerValue ->
            log.debug("copiedResponseHeaders: ${headerName}: ${headerValue}")
        }
    }
}
