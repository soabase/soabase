/**
 * Copyright 2014 Jordan Zimmerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.soabase.admin.rest;

import com.google.common.collect.Lists;
import io.soabase.core.SoaFeatures;
import io.soabase.core.features.attributes.AttributeKey;
import io.soabase.core.features.attributes.SoaDynamicAttributes;
import io.soabase.core.features.attributes.SoaWritableDynamicAttributes;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/soa/attributes")
public class AttributesResource
{
    private final SoaFeatures features;

    @Inject
    public AttributesResource(SoaFeatures features)
    {
        this.features = features;
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public Response delete(Attribute attribute)
    {
        SoaWritableDynamicAttributes attributes = getAttributes();
        attributes.remove(new AttributeKey(attribute.getKey(), attribute.getScope()));
        return Response.ok().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUpdate(Attribute attribute)
    {
        SoaWritableDynamicAttributes attributes = getAttributes();
        attributes.put(new AttributeKey(attribute.getKey(), attribute.getScope()), attribute.getValue());
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public List<Attribute> getAll()
    {
        List<Attribute> result = Lists.newArrayList();
        for ( Map.Entry<AttributeKey, Object> entry : getAttributes().getAll().entrySet() )
        {
            result.add(new Attribute(entry.getKey().getKey(), entry.getKey().getScope(), String.valueOf(entry.getValue())));
        }
        return result;
    }

    private SoaWritableDynamicAttributes getAttributes()
    {
        SoaDynamicAttributes attributes = features.getAttributes();
        if ( attributes instanceof SoaWritableDynamicAttributes )
        {
            return (SoaWritableDynamicAttributes)attributes;
        }

        // TODO logging
        throw new WebApplicationException("Attributes instance is not writable");
    }
}
