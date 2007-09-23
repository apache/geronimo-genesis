/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.geronimo.gshell.whisper.transport.ssl;

import java.net.URI;

import org.apache.geronimo.gshell.whisper.ssl.SSLContextFactory;
import org.apache.geronimo.gshell.whisper.transport.tcp.TcpTransport;
import org.apache.mina.common.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.SSLFilter;

/**
 * Provides TCP+SSL client-side support.
 *
 * @version $Rev$ $Date$
 */
public class SslTransport
    extends TcpTransport
{
    public SslTransport(final URI remote, final URI local) throws Exception {
        super(remote, local);
    }

    @Override
    protected void configure(final DefaultIoFilterChainBuilder chain) throws Exception {
        assert chain != null;

        super.configure(chain);

        SSLFilter sslFilter = new SSLFilter(getSslContextFactory().createClientContext());
        sslFilter.setUseClientMode(true);

        chain.addFirst(SSLFilter.class.getSimpleName(), sslFilter);
    }

    //
    // AutoWire Support, Setters exposed to support Plexus autowire()  Getters exposed to handle state checking.
    //

    private SSLContextFactory sslContextFactory;
    
    public void setSslContextFactory(final SSLContextFactory factory) {
        log.debug("Using SSL Context Factory: {}", factory);

        this.sslContextFactory = factory;
    }

    protected SSLContextFactory getSslContextFactory() {
        if (sslContextFactory == null) {
            throw new IllegalStateException("SSL context factory not bound");
        }

        return sslContextFactory;
    }
}