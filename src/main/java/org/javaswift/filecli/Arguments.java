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

    @Parameter(names={ "-tn", "--tenant-name"}, description="the name of the Swift tenant", required=true)
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
}
