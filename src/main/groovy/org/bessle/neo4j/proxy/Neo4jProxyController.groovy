package org.bessle.neo4j.proxy

@RestController
@RequestMapping("/db/data")
class Neo4jProxyController {
	static responseFormats = ['json']
    def neo4jProxyService

    @RequestMapping(value = "/cypher", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    String cypher(@RequestBody String requestCypher) {
        println "requestCypher: ${requestCypher}"
        def result = neo4jProxyService.getCypherResult(requestCypher)
        println "result: ${result}"
    	/*
    	HttpHeaders headers = new HttpHeaders()
    	return new ResponseEntity<String>(result, headers, HttpStatus.OK)
        */
    }
}
