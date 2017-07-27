package org.apache.sling.metadatahandler.impl;

import com.google.gson.*;
import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.metadatahandler.MetadataProcessor;
import org.apache.sling.metadatahandler.entities.NodeTypeWrapper;
import org.apache.sling.metadatahandler.utils.NodeTypeWrapperUtils;

import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.NodeTypeTemplate;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by yurov on 28.06.2017.
 */
@Component(name = "metadata-handler")
@Service
public class MetadataProcessorImpl implements MetadataProcessor {

    @Reference
    private SlingRepository repository;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public NodeType get(String typeName) throws RepositoryException, LoginException {
        try (ResourceResolver resourceResolver = getResourceResolver()) {
            final NodeTypeManager manager = getNodeTypeManager(getSession(resourceResolver));
            return manager.getNodeType(typeName);
        }
    }

    @Override
    public Collection<NodeType> getList() throws LoginException, RepositoryException {
        try (ResourceResolver resourceResolver = getResourceResolver()) {
            final NodeTypeManager manager = getNodeTypeManager(getSession(resourceResolver));
            final NodeTypeIterator nodeTypeIterator = manager.getAllNodeTypes();
            final List<NodeType> result = new ArrayList<>();
            while (nodeTypeIterator.hasNext()) {
                result.add(nodeTypeIterator.nextNodeType());
            }
            return result;
        }
    }

    @Override
    public NodeType[] addCnd(InputStream inputStream) throws RepositoryException, IOException, ParseException, LoginException {
        try (InputStreamReader reader = new InputStreamReader(inputStream);
             ResourceResolver resourceResolver = getResourceResolver()) {
            final NodeType[] nodeTypes = CndImporter.registerNodeTypes(reader, getSession(resourceResolver));
            return nodeTypes;
        }
    }

    @Override
    public void delete(String typeName) throws RepositoryException, LoginException {
        try (ResourceResolver resourceResolver = getResourceResolver()) {
            final NodeTypeManager manager = getNodeTypeManager(getSession(resourceResolver));
            manager.unregisterNodeType(typeName);
        }
    }

    @Override
    public void add(InputStream inputStream) throws RepositoryException, IOException, ParseException, LoginException {
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            final String jsonString = IOUtils.toString(reader);

            JsonElement element = new JsonParser().parse(jsonString);
            if (!element.isJsonObject()) {
                throw new IllegalArgumentException("Required json object");
            }
            JsonObject root = element.getAsJsonObject();
            if (!root.has("nodetypes")) {
                throw new IllegalArgumentException("Array nodetypes is required");
            }
            if (root.has("namespaces")) {
                registerNameSpaces(root.get("namespaces"));
                // register namespaces
            }
            registerNodeTypes(root.get("nodetypes"));
        }
    }

    private void registerNodeTypes(JsonElement nodetypes) throws RepositoryException, LoginException {
        if (!nodetypes.isJsonArray()) {
            throw new IllegalArgumentException("Node types should be placed in a array");
        }
        JsonArray array = nodetypes.getAsJsonArray();
        Gson gson = new Gson();


        try (ResourceResolver resourceResolver = getResourceResolver()) {

            final Session session = getSession(resourceResolver);
            final NodeTypeManager manager = getNodeTypeManager(session);
            try {
                for (JsonElement item : array) {
                    final NodeTypeWrapper wrapper = gson.fromJson(item, NodeTypeWrapper.class);
                    if (wrapper == null) {
                        throw new IllegalArgumentException("Couldn't parse node type");
                    }
                    final NodeTypeTemplate nodeTypeTemplate = NodeTypeWrapperUtils.wrapperToNodeTypeTemplate(wrapper, manager);
                    manager.registerNodeType(nodeTypeTemplate, true);
                }
                if (session.isLive() && session.hasPendingChanges()) {
                    session.save();
                }
            } catch (Exception ex) {
                if (session.isLive() && session.hasPendingChanges()) {
                    session.refresh(false);
                }
                // ex.printStackTrace();
                // caused exception
                throw ex;
            }
        }
    }

    /**
     * Register all the passed namespaces; existed are overwritten
     *
     * @param namespaces
     * @throws RepositoryException
     */
    private void registerNameSpaces(JsonElement namespaces) throws RepositoryException, LoginException {
        if (!namespaces.isJsonObject()) {
            throw new IllegalArgumentException("Namespaces should be placed in object as a map");
        }
        final JsonObject object = namespaces.getAsJsonObject();
        final NamespaceRegistry ns = getNamespaceRegistry();
        for (String key : object.keySet()) {
            final String value = object.get(key).getAsString();
            ns.registerNamespace(key, value);
        }
    }

    private NamespaceRegistry getNamespaceRegistry() throws javax.jcr.RepositoryException, LoginException {
        try (ResourceResolver resourceResolver = getResourceResolver()) {
            Session session = getSession(resourceResolver);
            //Session session = repository.loginAdministrative(null);
            return session.getWorkspace().getNamespaceRegistry();
        }
    }

    private Session getSession(final ResourceResolver resolver) throws javax.jcr.RepositoryException, LoginException {
        // TODO security workaround
        return getAdministrativeSession();
        //return resolver.adaptTo(Session.class);
    }

    @Deprecated
    private Session getAdministrativeSession() throws javax.jcr.RepositoryException, LoginException {
        return repository.loginAdministrative(null);
    }

    private ResourceResolver getResourceResolver() throws LoginException {
        final Map<String, Object> authInfo = new HashMap<>();
        authInfo.put(ResourceResolverFactory.USER, "admin");
        return resourceResolverFactory.getServiceResourceResolver(authInfo);
    }

    private NodeTypeManager getNodeTypeManager(final Session session) throws javax.jcr.RepositoryException {
        NodeTypeManager manager = session.getWorkspace().getNodeTypeManager();
        return manager;
    }
}
