/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.pulsar.client.impl.schema.generic;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import org.apache.pulsar.client.api.schema.GenericSchema;
import org.apache.pulsar.client.impl.LookupService;
import org.apache.pulsar.client.impl.PulsarClientImpl;
import org.apache.pulsar.client.impl.schema.AvroSchema;
import org.apache.pulsar.client.impl.schema.SchemaTestUtils;
import org.apache.pulsar.common.naming.TopicName;
import org.apache.pulsar.common.schema.SchemaInfo;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Unit test for {@link MultiVersionGenericSchemaProvider}.
 */
public class MultiVersionGenericSchemaProviderTest {

    private MultiVersionGenericSchemaProvider schemaProvider;

    @BeforeMethod
    public void setup() {
        PulsarClientImpl client = mock(PulsarClientImpl.class);
        when(client.getLookup()).thenReturn(mock(LookupService.class));
        schemaProvider = new MultiVersionGenericSchemaProvider(
                TopicName.get("persistent://public/default/my-topic"), client);
    }

    @Test
    public void testGetSchema() {
        CompletableFuture<Optional<SchemaInfo>> completableFuture = new CompletableFuture<>();
        SchemaInfo schemaInfo = AvroSchema.of(SchemaTestUtils.Foo.class).getSchemaInfo();
        completableFuture.complete(Optional.of(schemaInfo));
        when(schemaProvider.getPulsarClient().getLookup()
                .getSchema(
                        any(TopicName.class),
                        any(byte[].class)))
                .thenReturn(completableFuture);
        GenericSchema schema = schemaProvider.getSchema(new byte[0]);
        assertEquals(schema.getSchemaInfo(), schemaInfo);
    }
}
