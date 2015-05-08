package org.bessle.neo4j.proxy

import org.apache.http.Header
import org.springframework.http.HttpHeaders
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component


/**
 * Created by uwe on 07.05.15.
 */
@Slf4j
@Component
class HttpUtil {

    HttpHeaders copyResponseHeaders(Iterable<Header> backendResponseHeaders, List blacklist = [], Map additionalHeaders = [:]) {
        log.debug("copyResponseHeaders(... , ${blacklist} , ${additionalHeaders})")
        HttpHeaders copiedResponseHeaders = new HttpHeaders()
        backendResponseHeaders.each() { Header header ->
            if ( header.name in blacklist ) {
                log.trace("copyResponseHeaders(): do not copy header ${header.name}: ${header.value}")
            } else {
                copiedResponseHeaders.add(header.name, header.value)
                log.trace("copyResponseHeaders(): copy header ${header.name}: ${header.value}")
            }
        }
        additionalHeaders.each { String name, String value ->
            log.trace("copyRequestHeaders(): add additional header ${name}: ${value}")
            copiedResponseHeaders.add(name, value)
        }
        log.trace("copiedResponseHeaders: ${copiedResponseHeaders}")

        return copiedResponseHeaders
    }

    Map copyRequestHeaders(HttpHeaders clientRequestHeaders, List blacklist=[], Map additionalHeaders = [:]) {
        log.debug("copyRequestHeaders(... , ${blacklist} , ${additionalHeaders})")
        Map copiedRequestHeaders = [:]
        clientRequestHeaders.each() { String headerName, def headerValues ->
            if ( headerName in blacklist ) {
                log.debug("copyRequestHeaders(): do not copy header ${headerName}: ${headerValues}")
            } else {
                copiedRequestHeaders[headerName] = headerValues[0]
                log.debug("copyRequestHeaders(): copy header ${headerName}: ${headerValues}")
                if (headerValues.size() > 1) {
                    List nonCopiedHeaderValues = headerValues.remove(0)
                    log.warn("copyRequestHeaders(): do not copy header Values ${headerName}: ${nonCopiedHeaderValues}")
                }
            }
        }
        copiedRequestHeaders.putAll(additionalHeaders)
        log.debug("copiedRequestHeaders(): ${copiedRequestHeaders}")

        return copiedRequestHeaders
    }

}
