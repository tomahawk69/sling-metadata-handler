package org.apache.sling.metadatahandler;

import org.apache.sling.api.resource.AbstractResource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.spi.resource.provider.ResolveContext;
import org.apache.sling.spi.resource.provider.ResourceContext;
import org.apache.sling.spi.resource.provider.ResourceProvider;
import org.osgi.service.component.annotations.Component;

import java.util.Iterator;

import static org.apache.sling.metadatahandler.FacadeService.RESOURCE_TYPE;

/**
 * Created by yurov on 28.06.2017.
 * This class provided a custom path matching to be able work with an arbitrary calls like
 * /metadata
 * /metadata/<any type>
 */
@Component(service = {ResourceProvider.class}, immediate = true,
        property = {ResourceProvider.PROPERTY_ROOT + "=/metadata"}
)
public class MetadataResourceProvider extends ResourceProvider {

    @Override
    public org.apache.sling.api.resource.Resource getResource(ResolveContext ctx, final String path, ResourceContext resourceContext, org.apache.sling.api.resource.Resource parent) {
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
                return null;//resourceResolver;
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
