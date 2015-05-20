package org.bessle.neo4j.proxy

import groovyx.net.http.RESTClient
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.AbstractHttpClient
import org.apache.http.params.HttpParams

/**
 * Created by BessleU on 20.05.2015.
 */
class MultithreadedRESTClient extends RESTClient {
    protected AbstractHttpClient createClient( HttpParams params ) {
        def connManager = new MultiThreadedHttpConnectionManager()
        def connManagerParams = new HttpConnectionManagerParams()
        connManagerParams.maxTotalConnections = 50 // default is 20
        connManagerParams.defaultMaxConnectionsPerHost = 50 // default is 2
        connManager.params = connManagerParams
        new HttpClient(connManager)
    }
}
