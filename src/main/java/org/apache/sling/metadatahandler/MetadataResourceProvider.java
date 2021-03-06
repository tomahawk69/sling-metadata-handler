package org.apache.sling.metadatahandler;

import org.apache.sling.api.resource.AbstractResource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.wrappers.ResourceResolverWrapper;
import org.apache.sling.spi.resource.provider.ResolveContext;
import org.apache.sling.spi.resource.provider.ResourceContext;
import org.apache.sling.spi.resource.provider.ResourceProvider;
import org.osgi.service.component.annotations.Component;

import java.util.Iterator;

import static org.apache.sling.metadatahandler.FacadeService.RESOURCE_TYPE;
import static org.apache.sling.metadatahandler.FacadeService.ROOT_PATH;

/**
 * Created by yurov on 28.06.2017.
 * This class provided a custom path matching to be able work with an arbitrary calls like
 * /metadata
 * /metadata/<any type>
 */
@Component(service = {ResourceProvider.class}, immediate = true,
        property = {ResourceProvider.PROPERTY_ROOT + "=" + ROOT_PATH}
)
public class MetadataResourceProvider extends ResourceProvider {

    @Override
    public org.apache.sling.api.resource.Resource getResource(final ResolveContext ctx, final String path, final ResourceContext resourceContext, org.apache.sling.api.resource.Resource parent) {
        AbstractResource abstractResource;
        abstractResource = new AbstractResource() {

            @Override
            public String getResourceType() {
                return RESOURCE_TYPE;
            }

            @Override
            public String getResourceSuperType() {
                return null;
            }

            @Override
            public String getPath() {
                return path;
            }

            @Override
            public ResourceResolver getResourceResolver() {
                return ctx.getResourceResolver();
            }

            @Override
            public ResourceMetadata getResourceMetadata() {
                return new ResourceMetadata();
            }


        };

        return abstractResource;
    }

    @Override
    public Iterator<org.apache.sling.api.resource.Resource> listChildren(ResolveContext ctx, org.apache.sling.api.resource.Resource parent) {
        return null;
    }

}
