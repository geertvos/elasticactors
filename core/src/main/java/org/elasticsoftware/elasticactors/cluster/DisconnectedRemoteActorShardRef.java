/*
 * Copyright 2013 - 2017 The Original Authors
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

import org.elasticsoftware.elasticactors.ActorContainerRef;

import static java.lang.String.format;

/**
 * @author Joost van de Wijgerd
 */
public final class DisconnectedRemoteActorShardRef extends BaseDisconnectedActorRef {
    private static final String REFSPEC_FORMAT = "actor://%s/%s/shards/%d/%s";
    private final int shardId;

    DisconnectedRemoteActorShardRef(String clusterName, String actorSystemName, String actorId, int shardId) {
        super(actorId, clusterName, format(REFSPEC_FORMAT, clusterName, actorSystemName, shardId, actorId), actorSystemName);
        this.shardId = shardId;
    }

    @Override
    public String getActorPath() {
        return format("%s/shards/%d", actorSystemName, shardId);
    }

    @Override
    protected String getExceptionMessage() {
        return format("Remote Actor Cluster %s is not configured, ensure a correct remote configuration in the config.yaml", clusterName);
    }
}
