/*
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reactivex.netty.protocol.tcp.client.internal;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import io.reactivex.netty.channel.events.ConnectionEventListener;
import io.reactivex.netty.events.EventPublisher;
import io.reactivex.netty.events.EventSource;
import io.reactivex.netty.protocol.tcp.client.events.TcpClientEventListener;
import io.reactivex.netty.protocol.tcp.client.events.TcpClientEventPublisher;
import rx.Subscription;

public class TcpEventPublisherFactory implements EventPublisherFactory {

    public static final AttributeKey<EventPublisher> EVENT_PUBLISHER =
            AttributeKey.valueOf("rxnetty_tcp_client_event_publisher");
    public static final AttributeKey<TcpClientEventListener> TCP_CLIENT_EVENT_LISTENER =
            AttributeKey.valueOf("rxnetty_tcp_client_event_listener");
    public static final AttributeKey<ConnectionEventListener> CONNECTION_EVENT_LISTENER =
            AttributeKey.valueOf("rxnetty_tcp_client_conn_event_listener");
    private final TcpClientEventPublisher globalClientEventPublisher;

    public TcpEventPublisherFactory() {
        this(new TcpClientEventPublisher());
    }

    private TcpEventPublisherFactory(TcpClientEventPublisher globalClientEventPublisher) {
        this.globalClientEventPublisher = globalClientEventPublisher;
    }

    @Override
    public Subscription subscribe(TcpClientEventListener eventListener) {
        return globalClientEventPublisher.subscribe(eventListener);
    }

    @Override
    public final EventSource<TcpClientEventListener> call(Channel channel) {
        final TcpClientEventPublisher eventPublisher = new TcpClientEventPublisher();
        channel.attr(EVENT_PUBLISHER).set(eventPublisher);
        channel.attr(TCP_CLIENT_EVENT_LISTENER).set(eventPublisher);
        channel.attr(CONNECTION_EVENT_LISTENER).set(eventPublisher);
        eventPublisher.subscribe(globalClientEventPublisher);
        return eventPublisher;
    }

    @Override
    public TcpEventPublisherFactory copy() {
        return new TcpEventPublisherFactory(globalClientEventPublisher.copy());
    }

    @Override
    public TcpClientEventListener getGlobalClientEventPublisher() {
        return globalClientEventPublisher;
    }
}
