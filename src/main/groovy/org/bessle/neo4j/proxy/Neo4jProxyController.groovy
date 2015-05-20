package org.bessle.neo4j.proxy

import groovy.util.logging.Slf4j
import groovyx.net.http.HttpResponseDecorator
import org.apache.http.HttpResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus

@RestController
@RequestMapping("/db/data")
@Slf4j
class Neo4jProxyController {
    @Autowired
    Neo4jProxyService neo4jProxyService

    @Autowired
    HttpUtil httpUtil

    @RequestMapping(value = "/cypher", method = RequestMethod.POST)
    ResponseEntity<String> postCypher(HttpEntity<String> clientCypherRequest) {
        log.info("postCypher")
        return handleRequest(clientCypherRequest, RequestMethod.POST)

    }

    @RequestMapping(value = "/cypher", method = RequestMethod.OPTIONS)
    ResponseEntity<String> optionsCypher(HttpEntity<String> clientCypherRequest) {
        log.info("optionsCypher")
        return handleRequest(clientCypherRequest, RequestMethod.OPTIONS)
    }

    private ResponseEntity<String> handleRequest(HttpEntity<String> clientCypherRequest, RequestMethod clientRequestMethod) {
        // extract call parameter values
        String clientRequestCypher = clientCypherRequest.body
        HttpHeaders clientRequestHeaders = clientCypherRequest.headers

        // forward client request to backend
        HttpResponseDecorator backendResponse = neo4jProxyService.getCypherResult(clientRequestCypher, clientRequestHeaders, clientRequestMethod)
        String clientResponseBody = backendResponse.data
        HttpStatus clientResponseStatus = HttpStatus.valueOf(backendResponse.status)

        // construct client response headers
        HttpHeaders clientResponseHeaders = httpUtil.copyResponseHeaders(
                backendResponse.headers,
                [HttpHeaders.CONTENT_LENGTH],
                ["X-Test": "1234"]
        )
        if (backendResponse.isSuccess()) {
            clientResponseHeaders.setCacheControl("max-age=3600")
        }
        log.debug("clientResponseHeaders: ${clientResponseHeaders}")

        // return response
        return new ResponseEntity<String>(
                clientResponseBody,
                clientResponseHeaders,
                clientResponseStatus
        )
    }

}
