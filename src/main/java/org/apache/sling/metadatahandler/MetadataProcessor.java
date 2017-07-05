package org.apache.sling.metadatahandler;

import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.sling.api.resource.LoginException;

import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * Created by yurov on 28.06.2017.
 */
public interface MetadataProcessor {

    NodeType get(final String typeName) throws RepositoryException, LoginException;

    Collection<NodeType> getList() throws LoginException, RepositoryException;

    NodeType[] addCnd(InputStream inputStream) throws RepositoryException, IOException, ParseException, LoginException;

    void delete(final String typeName) throws RepositoryException, LoginException;

    void add(InputStream inputStream) throws RepositoryException, IOException, ParseException, LoginException;
}
