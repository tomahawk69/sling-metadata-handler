package org.apache.sling.metadatahandler.impl;

import com.google.gson.*;
import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.metadatahandler.MetadataProcessor;
import org.apache.sling.metadatahandler.entities.NodeTypeWrapper;

import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.nodetype.NodeTypeManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by yurov on 28.06.2017.
 */
@Component
@Service
public class MetadataProcessorImpl implements MetadataProcessor {

    @Reference
    private SlingRepository repository;

    @Override
    public NodeType get(String typeName) throws RepositoryException {
        final NodeTypeManager manager = getNodeTypeManager();
        return manager.getNodeType(typeName);
    }

    @Override
    public Collection<NodeType> getList() {
        try {
            final NodeTypeManager manager = getNodeTypeManager();
            final NodeTypeIterator nodeTypeIterator = manager.getAllNodeTypes();
            final List<NodeType> result = new ArrayList<>();
            while (nodeTypeIterator.hasNext()) {
                result.add(nodeTypeIterator.nextNodeType());
            }
            return result;
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public NodeType[] addCnd(InputStream inputStream) throws RepositoryException, IOException, ParseException {
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            final NodeType[] nodeTypes = CndImporter.registerNodeTypes(reader, getAdminSession());
            return nodeTypes;
        }
    }

    @Override
    public void delete(String typeName) throws RepositoryException {
        final NodeTypeManager manager = getNodeTypeManager();
        manager.unregisterNodeType(typeName);
    }

    @Override
    public void add(InputStream inputStream) throws RepositoryException, IOException, ParseException {
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

    private void registerNodeTypes(JsonElement nodetypes) {
        if (!nodetypes.isJsonArray()) {
            throw new IllegalArgumentException("Node types should be placed in a array");
        }
        JsonArray array = nodetypes.getAsJsonArray();
        Gson gson = new Gson();

        for (JsonElement item : array) {
            NodeTypeWrapper wrapper = gson.fromJson(item, NodeTypeWrapper.class);
            if (wrapper == null) {
                throw new IllegalArgumentException("Couldn't parse node type");
            }
        }
    }

    /**
     * Register all the passed namespaces; existed are overwritten
     *
     * @param namespaces
     * @throws RepositoryException
     */
    private void registerNameSpaces(JsonElement namespaces) throws RepositoryException {
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

    private NamespaceRegistry getNamespaceRegistry() throws javax.jcr.RepositoryException {
        Session session = getAdminSession();
        return session.getWorkspace().getNamespaceRegistry();
    }

    private Session getAdminSession() throws javax.jcr.RepositoryException {
        //repository.login()
        return repository.loginAdministrative(null);
    }

    private NodeTypeManager getNodeTypeManager() throws javax.jcr.RepositoryException {
        Session session = getAdminSession();
        NodeTypeManager manager = session.getWorkspace().getNodeTypeManager();
        return manager;
    }
}
