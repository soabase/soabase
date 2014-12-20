package io.soabase.admin;

import com.google.common.hash.Hashing;
import com.google.common.io.Resources;
import com.google.common.net.HttpHeaders;
import io.soabase.admin.components.ComponentManager;
import io.soabase.admin.components.TabComponent;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

public class IndexServlet extends HttpServlet
{
    public static final String SOA_TAB_PREFIX = "soa-tab-";

    private final ComponentManager componentManager;
    private final Charset charset;
    private volatile String indexFile;
    private volatile long lastModified;
    private volatile String eTag;

    public IndexServlet(ComponentManager componentManager)
    {
        this.componentManager = componentManager;
        charset = Charset.forName("UTF-8");
    }

    @Override
    public void init() throws ServletException
    {
        String localIndexFile;
        try
        {
            localIndexFile = Resources.toString(Resources.getResource("assets/index.html"), charset);
            localIndexFile = addTabs(localIndexFile, componentManager);
        }
        catch ( IOException e )
        {
            throw new ServletException(e);
        }
        this.indexFile = localIndexFile;
        // zero out the millis since the date we get back from If-Modified-Since will not have them
        lastModified = (System.currentTimeMillis() / 1000) * 1000;
        eTag = '"' + Hashing.murmur3_128().hashBytes(localIndexFile.getBytes()).toString() + '"';
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setStatus(200);
        response.setContentType("text/html");
        response.setContentLength(indexFile.length());
        response.setCharacterEncoding("UTF-8");
        response.setDateHeader(HttpHeaders.LAST_MODIFIED, lastModified);
        response.setHeader(HttpHeaders.ETAG, eTag);
        response.getWriter().print(indexFile);
    }

    private String addTabs(String localIndexFile, ComponentManager componentManager) throws IOException
    {
        StringBuilder tabs = new StringBuilder();
        StringBuilder ids = new StringBuilder();
        StringBuilder css = new StringBuilder();
        StringBuilder jss = new StringBuilder();
        StringBuilder content = new StringBuilder();
        for ( TabComponent tab : componentManager.getTabs() )
        {
            String id = SOA_TAB_PREFIX + tab.getId();
            tabs.append("<li id='").append(id).append("-li").append("'><a href=\"#").append(tab.getId()).append("\">").append(tab.getName()).append("</a></li>\n");
            ids.append("soaTabIds.push('").append(id).append("');\n");

            for ( String cssFile : tab.getCssPaths() )
            {
                css.append("<link rel=\"stylesheet\" href=\"").append(cssFile).append("\">");
            }
            for ( String jssFile : tab.getScriptPaths() )
            {
                jss.append("<script src=\"").append(jssFile).append("\"></script>");
            }

            String tabContent = Resources.toString(Resources.getResource(tab.getContentPath()), charset);   // TODO - handle classloader issues
            content.append("<div id=\"" + SOA_TAB_PREFIX).append(tab.getId()).append("\">").append(tabContent).append("</div>\n");
        }
        localIndexFile = localIndexFile.replace("$SOA_TABS$", tabs.toString());
        localIndexFile = localIndexFile.replace("$SOA_TABS_CONTENT$", content.toString());
        localIndexFile = localIndexFile.replace("$SOA_TAB_IDS$", ids.toString());
        localIndexFile = localIndexFile.replace("$SOA_CSS$", css.toString());
        localIndexFile = localIndexFile.replace("$SOA_JS$", jss.toString());
        localIndexFile = localIndexFile.replace("$SOA_NAME$", componentManager.getAppName());

        return localIndexFile;
    }
}
