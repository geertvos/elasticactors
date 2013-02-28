/*
 * Copyright 2013 Joost van de Wijgerd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.elasterix.elasticactors.cluster;

import org.elasterix.elasticactors.*;
import org.elasterix.elasticactors.messaging.InternalMessageImpl;
import org.elasterix.elasticactors.messaging.MessageQueue;
import org.elasterix.elasticactors.serialization.Serializer;
import org.springframework.beans.factory.annotation.Configurable;

import java.nio.ByteBuffer;

/**
 * @author Joost van de Wijgerd
 */
@Configurable
public class RemoteVirtualNode implements VirtualNode {
    private final ActorSystem actorSystem;
    private final PhysicalNode remoteNode;
    private final VirtualNodeKey virtualNodeKey;
    private final MessageQueue messageQueue;

    public RemoteVirtualNode(PhysicalNode remoteNode, ActorSystem actorSystem, int vNodeKey, MessageQueue remoteMessageQueue) {
        this.actorSystem = actorSystem;
        this.remoteNode = remoteNode;
        this.messageQueue = remoteMessageQueue;
        this.virtualNodeKey = new VirtualNodeKey(actorSystem.getName(),vNodeKey);
    }

    @Override
    public VirtualNodeKey getKey() {
        return virtualNodeKey;
    }

    public void sendMessage(ActorRef from, ActorRef to, Object message) throws Exception {
        Serializer<Object,ByteBuffer> messageSerializer = actorSystem.getSerializer(message.getClass());
        messageQueue.offer(new InternalMessageImpl(from,to,messageSerializer.serialize(message), message.getClass()));
        // signal remote node
        remoteNode.signalMessage(actorSystem,this);
    }


}