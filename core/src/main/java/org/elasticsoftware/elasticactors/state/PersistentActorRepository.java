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

package org.elasticsoftware.elasticactors.state;

import org.elasticsoftware.elasticactors.ShardKey;

import java.io.IOException;

/**
 * @author Joost van de Wijgerd
 */
public interface PersistentActorRepository {
    boolean contains(ShardKey shard,String actorId);

    void update(ShardKey shard,PersistentActor persistentActor) throws IOException;

    void delete(ShardKey shard,String actorId);

    PersistentActor get(ShardKey shard,String actorId) throws IOException;
}