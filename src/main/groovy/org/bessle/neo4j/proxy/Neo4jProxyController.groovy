package org.bessle.neo4j.proxy

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import groovy.util.logging.Slf4j
import groovyx.net.http.HttpResponseDecorator
import org.bessle.neo4j.proxy.util.HttpUtil
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

    @RequestMapping(method = RequestMethod.GET)
    ResponseEntity<String> get(HttpEntity<String> clientRequest) {
        log.info("get")
        return handleRequest(clientRequest, RequestMethod.GET)
    }


    private ResponseEntity<String> handleRequest(HttpEntity<String> clientCypherRequest, RequestMethod clientRequestMethod) {
        // extract call parameter values
        String clientRequestCypher = clientCypherRequest.body
        HttpHeaders clientRequestHeaders = clientCypherRequest.headers

        // forward client request to backend
        HttpResponseDecorator backendResponse = neo4jProxyService.postCypher(clientRequestCypher, clientRequestHeaders, clientRequestMethod)
        Gson gson = new GsonBuilder().create()
        String clientResponseBody = gson.toJson(backendResponse.data)
        log.info("clientResponseBody=${clientResponseBody.length()<=50 ? clientResponseBody : clientResponseBody.take(50)+'.....'} of type ${clientResponseBody.getClass().getName()}")
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
