package org.apache.sling.metadatahandler;

import org.apache.felix.utils.json.JSONWriter;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;

/**
 * Created by yurov on 28.06.2017.
 */
@Component(
        service = {Servlet.class},
        property = {
                //ServletResolverConstants.SLING_SERVLET_PATHS + "=/metadata",
                ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=" + FacadeService.RESOURCE_TYPE,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=GET",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=POST",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=DELETE",
        }
)
public class FacadeService extends SlingAllMethodsServlet {

    static final String RESOURCE_TYPE = "company/components/services/metadataService";


    @Reference
    private MetadataProcessor processor;

    private static final Logger LOGGER = LoggerFactory.getLogger(FacadeService.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("We are trying doGet");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        String[] pathInfo = request.getPathInfo().split("/");
        if (pathInfo.length == 2) {
            proceedGetList(request, response);
        } else if (pathInfo.length == 3) {
            proceedGet(request, response, pathInfo);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Expecting a request /metadata[/typeName]");
        }
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        final String[] pathInfo = request.getPathInfo().split("/");
        if (pathInfo.length != 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Expecting a request POST /metadata[/<typeName>]");
        } else {
            boolean isCnd = request.getRequestParameterMap().size() == 1 && request.getRequestParameterMap().getValue("type").getString().equals("cnd");
            if (isCnd) {
                doPostCnd(request, response);
            } else {
                doPostJson(request, response);
            }
        }
    }

    @Override
    protected void doDelete(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        final String[] pathInfo = request.getPathInfo().split("/");
        if (pathInfo.length != 3) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Expecting a request DELETE /metadata[/<typeName>]");
        } else {
            try (PrintWriter printWriter = response.getWriter()) {
                final String typeNameToDelete = pathInfo[2];
                processor.delete(typeNameToDelete);
                JSONWriter writer = new JSONWriter(printWriter);
                writer.array();
                writer.value(typeNameToDelete);
                writer.endArray();
                writer.flush();
            } catch (NoSuchNodeTypeException ex) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
            } catch (RepositoryException ex) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            }
        }
    }

    private void doPostCnd(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        try (InputStream requestInputStream = request.getInputStream();
             PrintWriter printWriter = response.getWriter()) {
            NodeType[] result = processor.addCnd(requestInputStream);
            JSONWriter writer = new JSONWriter(printWriter);
            writer.array();
            for (NodeType nodeType : result) {
                writer.value(nodeType.toString());
            }
            writer.endArray();
            writer.flush();
        } catch (ParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (IOException | RepositoryException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void doPostJson(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "doPostJson isn't implemented yet");
    }

    private void proceedGetList(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            Collection<NodeType> result = processor.getList();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.setStatus(200);
            out.write(result.toString());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void proceedGet(SlingHttpServletRequest request, SlingHttpServletResponse response, String[] pathInfo) throws IOException {
        try {
            String param = pathInfo[2];
            NodeType result = processor.get(param);
            try (PrintWriter out = response.getWriter()) {
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.setStatus(200);
                final JSONWriter writer = new JSONWriter(out);
                writeNodeTypeToJson(result, writer);
            }
        } catch (NoSuchNodeTypeException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Smth wrong with proceedGet", e);
        }
    }

    private void writeNodeTypeToJson(NodeType nodeType, JSONWriter writer) throws IOException {
        writer.object();
        writer.key("name").value(nodeType.getName());
        if (nodeType.isMixin()) {
            writer.key("mixin").value(true);
        }
        writer.key("supertypes").value(nodeType.getSupertypes());
        writer.key("declared_supertypes").value(nodeType.getDeclaredSupertypes());
        writer.key("primary_item_name").value(nodeType.getPrimaryItemName());
        if (nodeType.getChildNodeDefinitions().length > 0) {
            writer.key("child_definitions").value(nodeType.getChildNodeDefinitions());
        }
        writer.key("properties").array();
        for (PropertyDefinition propertyDefinition : nodeType.getPropertyDefinitions()) {
            writer.object();
            writer.key("name").value(propertyDefinition.getName());
            writer.key("required_type").value(getRequiredTypeName(propertyDefinition));
            if (propertyDefinition.getDefaultValues().length > 0) {
                writer.key("defaults").value(propertyDefinition.getDefaultValues());
            }
            writer.endObject();
        }
        writer.endArray();
        writer.endObject();
    }

    private String getRequiredTypeName(PropertyDefinition propertyDefinition) {
        return PropertyType.nameFromValue(propertyDefinition.getRequiredType());
    }
}
