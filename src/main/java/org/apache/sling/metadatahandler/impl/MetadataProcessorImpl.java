package org.apache.sling.metadatahandler.impl;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.nodetype.NodeTypeManager;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.metadatahandler.MetadataProcessor;

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
