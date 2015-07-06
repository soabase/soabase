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
package io.soabase.core.features.discovery.deployment;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.soabase.core.features.attributes.AttributeKey;
import io.soabase.core.features.attributes.DynamicAttributes;
import io.soabase.core.features.attributes.WritableDynamicAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.util.Set;

public class DefaultDeploymentGroupManager implements DeploymentGroupManager
{
    private final DynamicAttributes dynamicAttributes;
    private final Collection<String> instanceDeploymentGroups;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public static final String KEY_PREFIX = "_soabase_deployment_group";
    public static final String SEPARATOR = "_";

    public DefaultDeploymentGroupManager(DynamicAttributes dynamicAttributes, Collection<String> instanceDeploymentGroups)
    {
        this.dynamicAttributes = Preconditions.checkNotNull(dynamicAttributes, "dynamicAttributes cannot be null");
        instanceDeploymentGroups = Preconditions.checkNotNull(instanceDeploymentGroups, "instanceDeploymentGroups cannot be null");
        this.instanceDeploymentGroups = ImmutableSet.copyOf(instanceDeploymentGroups);
    }

    @Override
    public Collection<String> getInstanceGroups()
    {
        return instanceDeploymentGroups;
    }

    @Override
    public Collection<String> getKnownGroups(String serviceName)
    {
        serviceName = Preconditions.checkNotNull(serviceName, "serviceName cannot be null");
        String prefix = makeKeyPrefix(serviceName);

        Set<String> groups = Sets.newTreeSet();
        for ( String key : dynamicAttributes.getKeys() )
        {
            if ( key.startsWith(prefix) )
            {
                groups.add(key.substring(prefix.length()));
            }
        }
        return groups;
    }

    @Override
    public void ableGroup(String serviceName, String groupName, boolean enable)
    {
        serviceName = Preconditions.checkNotNull(serviceName, "serviceName cannot be null");
        Preconditions.checkNotNull(groupName, "groupName cannot be null");

        if ( dynamicAttributes instanceof WritableDynamicAttributes )
        {
            ((WritableDynamicAttributes)dynamicAttributes).put(new AttributeKey(makeKey(serviceName, groupName), ""), Boolean.toString(enable));
        }
        else
        {
            throw new UnsupportedOperationException("Dynamic attributes instance is not writable");
        }
    }

    @Override
    public boolean isAnyGroupEnabled(String serviceName, Collection<String> groups)
    {
        serviceName = Preconditions.checkNotNull(serviceName, "serviceName cannot be null");
        groups = Preconditions.checkNotNull(groups, "groups cannot be null");

        if ( groups.size() == 0 )
        {
            return true;    // special case - no groups means the default group
        }

        for ( String groupName : groups )
        {
            if ( isGroupEnabled(serviceName, groupName) )
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isGroupEnabled(String serviceName, String groupName)
    {
        if ( groupName == null )
        {
            return true;
        }

        serviceName = Preconditions.checkNotNull(serviceName, "serviceName cannot be null");
        String value = dynamicAttributes.getAttribute(makeKey(serviceName, groupName), "true");  // all groups are enabled by default
        try
        {
            return Boolean.parseBoolean(value);
        }
        catch ( Exception e )
        {
            log.warn(String.format("Bad deployment group setting. Group Name: %s - Value: %s", groupName, value));
        }
        return false;
    }

    private static String makeKeyPrefix(String serviceName)
    {
        return KEY_PREFIX + SEPARATOR + serviceName + SEPARATOR;
    }

    private static String makeKey(String serviceName, String groupName)
    {
        return makeKeyPrefix(serviceName) + groupName;
    }
}
