/*
 * Copyright 2013 - 2014 The Original Authors
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

package org.elasticsoftware.elasticactors.cluster;

import org.elasticsoftware.elasticactors.ActorContainer;
import org.elasticsoftware.elasticactors.ActorContainerRef;
import org.elasticsoftware.elasticactors.ActorRef;

import javax.annotation.Nullable;

import static java.lang.String.format;

/**
 *
 * @author  Joost van de Wijgerd
 */
public final class DisconnectedRemoteActorNodeRef implements ActorRef, ActorContainerRef {
    private final String clusterName;
    private final String actorSystemName;
    private final String nodeId;
    private final String actorId;
    private final String refSpec;

    public DisconnectedRemoteActorNodeRef(String clusterName, String actorSystemName, String nodeId, @Nullable String actorId) {
        this.clusterName = clusterName;
        this.actorSystemName = actorSystemName;
        this.nodeId = nodeId;
        this.actorId = actorId;
        this.refSpec = generateRefSpec(clusterName, actorSystemName, nodeId, actorId);
    }

    public static String generateRefSpec(String clusterName, String actorSystemName, String nodeId,String actorId) {
        if(actorId != null) {
            return String.format("actor://%s/%s/nodes/%s/%s",clusterName,actorSystemName,nodeId,actorId);
        } else {
            return String.format("actor://%s/%s/nodes/%s",clusterName,actorSystemName,nodeId);
        }
    }

    @Override
    public String getActorCluster() {
        return clusterName;
    }

    @Override
    public String getActorPath() {
        return String.format("%s/nodes/%s",actorSystemName,nodeId);
    }

    public String getActorId() {
        return actorId;
    }

    @Override
    public void tell(Object message, ActorRef sender) {
        tell(message);
    }

    @Override
    public void tell(Object message) {
        throw new IllegalStateException(format("Remote Actor Node %s cannot be reached, make sure to configure the remote actor system [%s] in the configuration",nodeId, clusterName));
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public ActorContainer getActorContainer() {
        throw new IllegalStateException(format("Remote Actor Node %s cannot be reached, make sure to configure the remote actor system [%s] in the configuration",nodeId, clusterName));
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof ActorRef && this.toString().equals(o.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return this.refSpec;
    }
}
