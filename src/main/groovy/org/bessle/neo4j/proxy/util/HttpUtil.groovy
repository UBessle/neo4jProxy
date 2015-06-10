package org.bessle.neo4j.proxy.util

import org.apache.http.Header
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component

import groovy.util.logging.Slf4j


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
        log.trace("copyRequestHeaders(... , ${blacklist} , ${additionalHeaders})")
        Map copiedRequestHeaders = [:]
        clientRequestHeaders.each() { String headerName, def headerValues ->
            if ( headerName in blacklist ) {
                log.trace("copyRequestHeaders(): do not copy header ${headerName}: ${headerValues}")
            } else {
                copiedRequestHeaders[headerName] = headerValues[0]
                log.trace("copyRequestHeaders(): copy header ${headerName}: ${headerValues}")
                if (headerValues.size() > 1) {
                    List nonCopiedHeaderValues = headerValues.remove(0)
                    log.warn("copyRequestHeaders(): do not copy header Values ${headerName}: ${nonCopiedHeaderValues}")
                }
            }
        }
        copiedRequestHeaders.putAll(additionalHeaders)
        log.trace("copiedRequestHeaders(): ${copiedRequestHeaders}")

        return copiedRequestHeaders
    }

}
