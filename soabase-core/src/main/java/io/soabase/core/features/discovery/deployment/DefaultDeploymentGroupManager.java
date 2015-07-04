package io.soabase.core.features.discovery.deployment;

import com.google.common.base.Preconditions;
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
    private final Logger log = LoggerFactory.getLogger(getClass());

    public static final String KEY_PREFIX = "_soabase_deployment_group_";

    public DefaultDeploymentGroupManager(DynamicAttributes dynamicAttributes)
    {
        this.dynamicAttributes = dynamicAttributes;
    }

    @Override
    public Collection<String> getKnownGroups()
    {
        Set<String> groups = Sets.newTreeSet();
        for ( String key : dynamicAttributes.getKeys() )
        {
            if ( key.startsWith(KEY_PREFIX) )
            {
                groups.add(key.substring(KEY_PREFIX.length()));
            }
        }
        return groups;
    }

    @Override
    public void ableGroup(String groupName, boolean enable)
    {
        Preconditions.checkNotNull(groupName, "groupName cannot be null");

        if ( dynamicAttributes instanceof WritableDynamicAttributes )
        {
            ((WritableDynamicAttributes)dynamicAttributes).put(new AttributeKey(makeKey(groupName), ""), Boolean.toString(enable));
        }

        throw new UnsupportedOperationException("Dynamic attributes instance is not writable");
    }

    @Override
    public boolean isAnyGroupEnabled(Collection<String> groups)
    {
        if ( groups.size() == 0 )
        {
            return true;    // special case - no groups means the default group
        }

        for ( String groupName : groups )
        {
            if ( isGroupEnabled(groupName) )
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isGroupEnabled(String groupName)
    {
        if ( groupName == null )
        {
            return true;
        }

        String value = dynamicAttributes.getAttribute(makeKey(groupName), "true");  // all groups are enabled by default
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

    private static String makeKey(String groupName)
    {
        return KEY_PREFIX + groupName;
    }
}
