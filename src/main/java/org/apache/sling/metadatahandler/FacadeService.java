package org.apache.sling.metadatahandler;

import com.google.gson.Gson;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.felix.scr.annotations.Property;
import org.apache.http.entity.ContentType;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.auth.core.AuthConstants;
import org.apache.sling.metadatahandler.entities.NodeTypeWrapper;

import org.apache.sling.metadatahandler.utils.NodeTypeWrapperUtils;
import org.apache.sling.serviceusermapping.ServiceUserMapped;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeType;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;

import static org.apache.sling.metadatahandler.FacadeService.ROOT_PATH;

/**
 * Created by yurov on 28.06.2017.
 */
@Component(
        service = {Servlet.class},
        property = {
                ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=" + FacadeService.RESOURCE_TYPE,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_DELETE,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_PUT,
                AuthConstants.AUTH_REQUIREMENTS + "=" + ROOT_PATH
       },
        reference = {
                @Reference(name = "ng-admin", service = ServiceUserMapped.class)
        }
)
public class FacadeService extends SlingAllMethodsServlet {

    static final String RESOURCE_TYPE = "company/components/services/metadataService";
    private static final String PATH_DELIMITER = "/";
    public static final String ROOT_PATH = "/metadata";

    @Reference
    private MetadataProcessor processor;


    private static final Logger LOGGER = LoggerFactory.getLogger(FacadeService.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("We are trying doGet");
        String[] pathInfo = splitPath(request);
        if (pathInfo.length == 2) {
            proceedGetList(response);
        } else if (pathInfo.length == 3) {
            proceedGet(response, pathInfo);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Expecting a request /metadata[/typeName]");
        }
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        final String[] pathInfo = splitPath(request);
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
        final String[] pathInfo = splitPath(request);
        if (pathInfo.length != 3) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Expecting a request DELETE /metadata[/<typeName>]");
        } else {
            try {
                final String typeNameToDelete = pathInfo[2];
                processor.delete(typeNameToDelete);
                setResponseOk(response, ContentType.DEFAULT_TEXT.toString());
                try (PrintWriter printWriter = response.getWriter()) {
                    printWriter.write(typeNameToDelete);
                }
            } catch (NoSuchNodeTypeException ex) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
            } catch (RepositoryException ex) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            } catch (LoginException ex) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
            }
        }
    }

    @Override
    protected void doPut(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Service doesn't support PUT method");
    }

    private void doPostCnd(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
        try (InputStream requestInputStream = request.getInputStream();
             PrintWriter printWriter = response.getWriter()) {
            NodeType[] added = processor.addCnd(requestInputStream);
            NodeType[] result = ArrayUtils.addAll(added);

            int i = 100;
            // execute while result is not empty to make sure the all VALID elements added
            while (added.length > 0) {
                added = processor.addCnd(requestInputStream);
                ArrayUtils.addAll(result, added);
                if (i-- < 0) {
                    // just in case
                    break;
                }
            }

            setResponseOk(response, ContentType.APPLICATION_JSON.toString());

            // just write an array of names of added node types
            printWriter.write(Arrays.toString(result));
        } catch (ParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (LoginException ex) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
        } catch (IOException | RepositoryException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void doPostJson(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
        try (InputStream requestInputStream = request.getInputStream()) {
            processor.add(requestInputStream);
            setResponseOk(response, ContentType.APPLICATION_JSON.toString());
            try (PrintWriter printWriter = response.getWriter()) {
                printWriter.write("Done");
            }

        } catch (RepositoryException e) {
            if (e.getCause() == null) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, String.format("%s (%s)", e.getMessage(), e.getCause().getMessage()));
            }
        } catch (LoginException ex) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
        } catch (ParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void proceedGetList(final SlingHttpServletResponse response) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            final Collection<NodeType> result = processor.getList();
            setResponseOk(response, ContentType.APPLICATION_JSON.toString());
            // just write an array of strings
            out.write(result.toString());
        } catch (final LoginException ex) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
        } catch (final Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    private void proceedGet(final SlingHttpServletResponse response, final String[] pathInfo) throws IOException {
        try {
            String param = pathInfo[2];
            NodeType result = processor.get(param);
            try (PrintWriter out = response.getWriter()) {
                setResponseOk(response, ContentType.APPLICATION_JSON.toString());
                out.write(nodeTypeToJson(result));
            }
        } catch (LoginException ex) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
        } catch (NoSuchNodeTypeException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error("Smth wrong with proceedGet", ex);
        }
    }

    private String[] splitPath(final SlingHttpServletRequest request) {
        return request.getPathInfo().split(PATH_DELIMITER);
    }

    private String nodeTypeToJson(final NodeType nodeType) throws IOException {
        final NodeTypeWrapper nodeTypeWrapper = NodeTypeWrapperUtils.nodeTypeToWrapper(nodeType);
        return new Gson().toJson(nodeTypeWrapper);
    }

    private void setResponseOk(final SlingHttpServletResponse response, final String contentType) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("utf-8");
        if (contentType != null) {
            response.setContentType(contentType);
        }
    }
}
