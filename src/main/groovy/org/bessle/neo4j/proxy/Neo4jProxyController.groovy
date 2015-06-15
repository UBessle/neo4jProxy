package org.bessle.neo4j.proxy

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import groovy.json.JsonOutput
import groovy.util.logging.Slf4j
import groovyx.net.http.HttpResponseDecorator
import org.apache.http.HttpResponse
import org.bessle.neo4j.proxy.util.HttpUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/db/data")
@Slf4j
class Neo4jProxyController {
    @Autowired
    Neo4jProxyService neo4jProxyService

    @Autowired
    HttpUtil httpUtil

    @RequestMapping(value = "/cypher", method = [RequestMethod.POST, RequestMethod.OPTIONS])
    ResponseEntity<String> postCypher(HttpEntity<String> clientCypherRequest, HttpMethod clientRequestMethod) {
        log.info("postCypher")

        // extract call parameter values
        String cypher = clientCypherRequest.body
        log.info("cypher query = ${cypher}")
        HttpHeaders clientRequestHeaders = clientCypherRequest.headers

        // forward client request to backend
        HttpResponseDecorator backendResponse = neo4jProxyService.postCypher(cypher, clientRequestHeaders, clientRequestMethod)
        if (cypher =~ / set /) {
            neo4jProxyService.clearCache()
        }

        // process backend response
        String clientResponseBody = JsonOutput.toJson(backendResponse.data)
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


    @RequestMapping(value="**", method = [RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE])
    ResponseEntity<String>  proxyRequest(
            HttpServletRequest clientRequest,
            @RequestHeader HttpHeaders clientRequestHeaders,
            HttpMethod clientRequestMethod,
            @RequestBody clientRequestBody)
    {
        log.info("clientRequestMethod =${clientRequestMethod}")
        String clientRequestURI = clientRequest.getRequestURI()
        log.info("clientRequestURI = ${clientRequestURI}")
        String requestURL = clientRequest.getRequestURL()
        log.info("requestURL = ${requestURL}")
        String servletPath = clientRequest.getServletPath();
        log.info("servletPath = ${servletPath}")

        HttpResponseDecorator backendResponse = neo4jProxyService.doNeo4jRequest(clientRequestURI, clientRequestHeaders, clientRequestMethod, clientRequestBody)
        if (clientRequestMethod.toString() in ['POST','PUT','PATCH','DELETE']) {
            neo4jProxyService.clearCache()
        }

        String clientResponseBody = JsonOutput.toJson(backendResponse.data)
        log.info("proxyRequest(): clientResponseBody=${clientResponseBody.length()<=50 ? clientResponseBody : clientResponseBody.take(50)+'.....'} of type ${clientResponseBody.getClass().getName()}")
        HttpStatus clientResponseStatus = HttpStatus.valueOf(backendResponse.status)

        // construct client response headers
        HttpHeaders clientResponseHeaders = httpUtil.copyResponseHeaders(
                backendResponse.headers,
                [HttpHeaders.CONTENT_LENGTH],
                ["X-Test": "1234"]
        )
        if (backendResponse.isSuccess() && clientRequestMethod.toString() in ['GET','HEAD','OPTIONS']) {
            clientResponseHeaders.setCacheControl("max-age=3600")
        }
        log.debug("proxyRequest(): clientResponseHeaders: ${clientResponseHeaders}")

        // return response
        return new ResponseEntity<String>(
                clientResponseBody,
                clientResponseHeaders,
                clientResponseStatus
        )
    }

    @RequestMapping(value="**", method = [RequestMethod.GET, RequestMethod.HEAD, RequestMethod.OPTIONS])
    ResponseEntity<String>  proxySimpleRequest(
            HttpServletRequest clientRequest,
            @RequestHeader HttpHeaders clientRequestHeaders,
            HttpMethod clientRequestMethod)
    {
        log.info("proxySimpleRequest: clientRequestMethod =${clientRequestMethod}")
        String clientRequestURI = clientRequest.getRequestURI()
        log.info("proxySimpleRequest: clientRequestURI = ${clientRequestURI}")
        String requestURL = clientRequest.getRequestURL()
        log.info("proxySimpleRequest: requestURL = ${requestURL}")
        String servletPath = clientRequest.getServletPath();
        log.info("proxySimpleRequest: servletPath = ${servletPath}")

        HttpResponseDecorator backendResponse = neo4jProxyService.doNeo4jRequest(clientRequestURI, clientRequestHeaders, clientRequestMethod, null)
        if (clientRequestMethod.toString() in ['POST','PUT','PATCH','DELETE']) {
            neo4jProxyService.clearCache()
        }

        String clientResponseBody = JsonOutput.toJson(backendResponse.data)
        log.info("proxySimpleRequest: clientResponseBody=${clientResponseBody.length()<=50 ? clientResponseBody : clientResponseBody.take(50)+'.....'} of type ${clientResponseBody.getClass().getName()}")
        HttpStatus clientResponseStatus = HttpStatus.valueOf(backendResponse.status)

        // construct client response headers
        HttpHeaders clientResponseHeaders = httpUtil.copyResponseHeaders(
                backendResponse.headers,
                [HttpHeaders.CONTENT_LENGTH],
                ["X-Test": "1234"]
        )
        if (backendResponse.isSuccess() && clientRequestMethod.toString() in ['GET','HEAD','OPTIONS']) {
            clientResponseHeaders.setCacheControl("max-age=3600")
        }
        log.debug("proxySimpleRequest: clientResponseHeaders: ${clientResponseHeaders}")

        // return response
        return new ResponseEntity<String>(
                clientResponseBody,
                clientResponseHeaders,
                clientResponseStatus
        )
    }

}
