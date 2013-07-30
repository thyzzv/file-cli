package org.javaswift.filecli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(separators = "=")
public class Arguments {

    @Parameter(names={ "-u", "--username"}, description="username in Swift", required=true)
    private String username;

    @Parameter(names={ "-p", "--password"}, description="password in Swift", required=true)
    private String password;

    @Parameter(names={ "-url" }, description="the Keystone URL to authenticate against", required=true)
    private String url;

    @Parameter(names={ "-ti", "--tenant-id"}, description="the ID of the Swift tenant")
    private String tenantId;

    @Parameter(names={ "-tn", "--tenant-name"}, description="the name of the Swift tenant")
    private String tenantName;

    @Parameter(names={ "-f", "--file"}, description="the path to the file to be uploaded")
    private String file;

    @Parameter(names={ "-c", "--container"}, description="the container to upload the file to", required=true)
    private String container;

    @Parameter(names={ "-h", "--host"}, description="host to use for showing public URLs")
    private String host;

    @Parameter(names={ "--prefix"}, description="only listing files starting with prefix")
    private String prefix;

    @Parameter(names={ "--help"}, description="shows the parameters for this tool", help = true)
    private boolean help;

    @Parameter(names={ "--allow-override" }, description="allows existing file to be overwritten")
    private boolean allowOverride;

    @Parameter(names={ "-d", "--delete" }, description="delete the file from Swift")
    private String deleteFile;

    @Parameter(names={ "--hash-password" }, description="hash password to set on the account")
    private String hashPassword;

    @Parameter(names={ "--temp-url" }, description="instead of showing the public URL, show the temp URL")
    private boolean showTempUrl;

    @Parameter(names={ "--seconds" }, description="numbers of seconds a temp URL may be active")
    private long seconds = 86400; // Default is one day

    @Parameter(names={ "--server" }, description="this parameter starts up as a webservice")
    private boolean server;

    @Parameter(names={ "--port" }, description="port of the server to start up under")
    private int port=4567;

    @Parameter(names={ "--redirect" }, description="URL to which a redirect will take place after an upload")
    private String redirectUrl;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public String getFile() {
        return file;
    }

    public String getContainer() {
        return container;
    }

    public boolean isHelp() {
        return help;
    }

    public String getHost() {
        return host;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isAllowOverride() {
        return allowOverride;
    }

    public String getDeleteFile() {
        return deleteFile;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public boolean isShowTempUrl() {
        return showTempUrl;
    }

    public long getSeconds() {
        return seconds;
    }

    public boolean isServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}
