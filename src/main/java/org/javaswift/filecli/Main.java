package org.javaswift.filecli;

import com.beust.jcommander.JCommander;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.client.factory.TempUrlHashPrefixSource;
import org.javaswift.joss.model.Account;
import org.javaswift.joss.model.Container;
import org.javaswift.joss.model.StoredObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

public class Main {

    public static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        Main main = new Main();
        Arguments arguments = main.determineArguments(args);
        if (arguments == null) {
            return;
        }
        Account account = main.createAccount(arguments);

        Container container = account.getContainer(arguments.getContainer());
        if (!container.exists()) {
            container.create();
        }

        if (arguments.isServer()) {
            main.startServer(arguments, account);
        } else if (arguments.getFile() != null) { // Upload file
            main.uploadFile(arguments, container);
        } else if (arguments.getDeleteFile() != null) { // Delete file
            main.deleteFile(arguments, container);
        } else { // List files
            main.listFiles(arguments, container);
        }

    }

    private Arguments determineArguments(String[] args) {
        Arguments arguments = new Arguments();
        final JCommander commander;
        try {
            commander = new JCommander(arguments, args);
        } catch (Exception err) {
            LOG.error(err.getMessage());
            return null;
        }

        if (arguments.isHelp()) {
            commander.usage();
            return null;
        }
        return arguments;
    }

    private Account createAccount(Arguments arguments) {

        LOG.info("Executing with "+
                "tenant name "+arguments.getTenantName()+
                ", tenant ID "+arguments.getTenantId()+
                " and usr/pwd "+arguments.getUsername()+"/"+arguments.getPassword()+"@"+arguments.getUrl());

        return new AccountFactory()
                .setUsername(arguments.getUsername())
                .setPassword(arguments.getPassword())
                .setAuthUrl(arguments.getUrl())
                .setPublicHost(arguments.getHost())
                .setTenantId(arguments.getTenantId())
                .setTenantName(arguments.getTenantName())
                .setHashPassword(arguments.getHashPassword())
                .setTempUrlHashPrefixSource(TempUrlHashPrefixSource.INTERNAL_URL_PATH)
                .createAccount();
    }

    private void startServer(final Arguments arguments, final Account account) {
        Spark.setPort(arguments.getPort());

        Spark.get(new Route("/") {
            @Override
            public Object handle(Request request, Response response) {
//                String accept = request.raw().getHeader("Accept");
                response.type("text/html");
                response.status(200);
                Map<String, Object> values = new TreeMap<>();
                values.put("containers", convertAccountToList(account, arguments));
                return callTemplate("containers.ftl", values);
            }
        });

        Spark.get(new Route("/container/:container") {
            @Override
            public Object handle(Request request, Response response) {
                response.type("text/html");
                Container container = account.getContainer(request.params(":container"));
                if (!container.exists()) {
                    return notFound(response, "Container", container.getName());
                }
                Map<String, Object> values = new TreeMap<>();
                values.put("container", container.getName());
                values.put("objects", convertContainerToList(container, arguments));
                return callTemplate("objects.ftl", values);

            }
        });

        Spark.get(new Route("/object/:container/:object") {
            @Override
            public Object handle(Request request, Response response) {
                response.type("text/html");
                Container container = account.getContainer(request.params(":container"));
                StoredObject object = container.getObject(request.params(":object"));
                if (!object.exists()) {
                    return notFound(response, "Object", object.getName());
                }
                LOG.info("Drafting temp URL for "+object.getPath());
                return object.getTempGetUrl(arguments.getSeconds());
            }
        });
    }

    private String notFound(Response response, String entityType, String entityName) {
        response.status(404);
        Map<String, Object> values = new TreeMap<>();
        values.put("entityType", entityType);
        values.put("entityName", entityName);
        return callTemplate("notfound.ftl", values);
    }

    private List<Map<String,Object>> convertAccountToList(Account account, Arguments arguments) {
        List<Map<String,Object>> containers = new ArrayList<>();
        for (Container container : account.list()) {
            Map<String,Object> containerMap = new TreeMap<>();
            containerMap.put("name", container.getName());
            containerMap.put("objects", convertContainerToList(container, arguments));
            containers.add(containerMap);
        }
        return containers;
    }

    private List<Map<String, Object>> convertContainerToList(Container container, Arguments arguments) {
        List<Map<String,Object>> objects = new ArrayList<>();
        for (StoredObject object : container.list()) {
            Map<String,Object> objectMap = new TreeMap<>();
            objectMap.put("name", object.getName());
            objectMap.put("size", longToBytes(object.getContentLength()));
            objectMap.put("tempUrl", encodeUrl(object.getTempGetUrl(arguments.getSeconds())));
            objects.add(objectMap);
        }
        return objects;
    }

    private String encodeUrl(String url) {
        return url.replace("&", "&amp;");
    }

    private String callTemplate(String templateName, Map<String, Object> values) {
        Configuration configuration = new Configuration();
        configuration.setClassForTemplateLoading(Main.class, "/");
        StringWriter writer = new StringWriter();
        try {
            Template template = configuration.getTemplate(templateName);
            template.process(values, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    private void listFiles(Arguments arguments, Container container) {
        for (StoredObject object : container.list(arguments.getPrefix(), null, -1)) {
            System.out.println("* "+object.getName() + " ("+ longToBytes(object.getContentLength())+") -> "+
                    (arguments.isShowTempUrl() ? object.getTempGetUrl(arguments.getSeconds()) : object.getPublicURL()));
        }
    }

    private void deleteFile(Arguments arguments, Container container) {
        StoredObject object = container.getObject(arguments.getDeleteFile());
        object.delete();
        LOG.info(object.getName() +" deleted from Swift");
    }

    private void uploadFile(Arguments arguments, Container container) {
        File uploadFile = new File(arguments.getFile());
        StoredObject object = container.getObject(uploadFile.getName());
        if (object.exists() && !arguments.isAllowOverride()) {
            LOG.error("File already exists. Upload cancelled");
            return;
        }
        object.uploadObject(uploadFile);
        System.out.println(object.getPublicURL());
    }

    public static String longToBytes(long bytesUsed) {
        String suffix = "B";
        if (bytesUsed / 1024 > 0) {
            bytesUsed /= 1024;
            suffix = "KB";
        }
        if (bytesUsed / 1024 > 0) {
            bytesUsed /= 1024;
            suffix = "MB";
        }
        if (bytesUsed / 1024 > 0) {
            bytesUsed /= 1024;
            suffix = "GB";
        }
        if (bytesUsed / 1024 > 0) {
            bytesUsed /= 1024;
            suffix = "TB";
        }
        return bytesUsed + " " + suffix;
    }

}
