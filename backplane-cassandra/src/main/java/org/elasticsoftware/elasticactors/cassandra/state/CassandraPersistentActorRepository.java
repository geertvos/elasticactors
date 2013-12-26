/*
 * Copyright 2013 the original authors
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

package org.elasticsoftware.elasticactors.cassandra.state;

import me.prettyprint.cassandra.serializers.ByteBufferSerializer;
import me.prettyprint.cassandra.serializers.BytesArraySerializer;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.hector.api.beans.HColumn;
import org.elasticsoftware.elasticactors.ShardKey;
import org.elasticsoftware.elasticactors.serialization.Deserializer;
import org.elasticsoftware.elasticactors.serialization.Serializer;
import org.elasticsoftware.elasticactors.state.PersistentActor;
import org.elasticsoftware.elasticactors.state.PersistentActorRepository;

import java.io.IOException;


/**
 * @author Joost van de Wijgerd
 */
public final class CassandraPersistentActorRepository implements PersistentActorRepository {
    private ColumnFamilyTemplate<String,String> columnFamilyTemplate;
    private Deserializer<byte[],PersistentActor> deserializer;
    private Serializer<PersistentActor,byte[]> serializer;

    @Override
    public boolean contains(ShardKey shard, String actorId) {
        return columnFamilyTemplate.querySingleColumn(shard.toString(),actorId, ByteBufferSerializer.get()) != null;
    }

    @Override
    public void update(ShardKey shard, PersistentActor persistentActor) throws IOException {
        ColumnFamilyUpdater<String,String> updater = columnFamilyTemplate.createUpdater(shard.toString());
        updater.setByteArray(persistentActor.getSelf().getActorId(), serializer.serialize(persistentActor));
        columnFamilyTemplate.update(updater);
    }

    @Override
    public void delete(ShardKey shard, String actorId) {
        columnFamilyTemplate.deleteColumn(shard.toString(),actorId);
    }

    @Override
    public PersistentActor<ShardKey> get(ShardKey shard, String actorId) throws IOException {
        HColumn<String,byte[]> column = columnFamilyTemplate.querySingleColumn(shard.toString(), actorId, BytesArraySerializer.get());
        if(column != null) {
            return deserializer.deserialize(column.getValue());
        } else {
            return null;
        }
    }

    public void setColumnFamilyTemplate(ColumnFamilyTemplate<String, String> columnFamilyTemplate) {
        this.columnFamilyTemplate = columnFamilyTemplate;
    }

    public void setDeserializer(Deserializer deserializer) {
        this.deserializer = deserializer;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }
}
